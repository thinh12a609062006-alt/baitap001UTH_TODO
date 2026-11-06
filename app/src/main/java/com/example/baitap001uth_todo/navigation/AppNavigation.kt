package com.example.baitap001uth_todo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.baitap001uth_todo.data.AuthManager
import com.example.baitap001uth_todo.data.OnboardingRepository
import com.example.baitap001uth_todo.model.AppInfo
import com.example.baitap001uth_todo.screens.EmailLoginScreen
import com.example.baitap001uth_todo.screens.HomeScreen
import com.example.baitap001uth_todo.screens.LoginScreen
import com.example.baitap001uth_todo.screens.OnboardingScreen
import com.example.baitap001uth_todo.screens.PhoneLoginScreen
import com.example.baitap001uth_todo.screens.ProfileScreen
import com.example.baitap001uth_todo.screens.SplashScreen
import com.example.baitap001uth_todo.viewmodel.HomeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authManager = remember { AuthManager() }
    val repository = remember { OnboardingRepository() }
    val homeViewModel: HomeViewModel = viewModel()

    val appInfo = AppInfo(
        name = "UTH Todo",
        tagline = "Việc học, việc nhà – gọn gàng mỗi ngày",
        version = "Phiên bản 1.0"
    )

    // Đưa về màn đăng nhập và xoá hết các màn trước (không Back lại được)
    val goToLogin = {
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // ===== Splash =====
        composable(Routes.SPLASH) {
            SplashScreen(
                appInfo = appInfo,
                onFinished = {
                    navController.navigate(Routes.onboarding(1)) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ===== Onboarding =====
        composable(
            route = Routes.ONBOARDING,
            arguments = listOf(navArgument("pageId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pageId = backStackEntry.arguments?.getInt("pageId") ?: 1
            val page = repository.getPage(pageId - 1)
            val total = repository.getTotalPages()

            // Xong Onboarding: đã đăng nhập -> Home, chưa -> Đăng nhập
            val goNext = {
                val target = if (authManager.currentUser != null) Routes.HOME else Routes.LOGIN
                navController.navigate(target) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }

            OnboardingScreen(
                page = page,
                totalPages = total,
                onNext = {
                    if (page.isLast(total)) goNext()
                    else navController.navigate(Routes.onboarding(pageId + 1))
                },
                onSkip = { goNext() }
            )
        }

        // ===== Đăng nhập =====
        composable(Routes.LOGIN) {
            LoginScreen(
                authManager = authManager,
                onEmailClick = { navController.navigate(Routes.EMAIL) },
                onPhoneClick = { navController.navigate(Routes.PHONE) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EMAIL) {
            EmailLoginScreen(
                authManager = authManager,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PHONE) {
            PhoneLoginScreen(
                authManager = authManager,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ===== Trang chủ (YÊU CẦU ĐĂNG NHẬP) =====
        composable(Routes.HOME) {
            val user = authManager.currentUser
            if (user == null) {
                // Chưa đăng nhập -> không cho vào, đẩy về màn đăng nhập
                LaunchedEffect(Unit) { goToLogin() }
                return@composable
            }

            val homeViewModel: HomeViewModel = viewModel()
            val name = user.displayName?.takeIf { it.isNotBlank() }
                ?: user.email?.substringBefore("@")
                ?: ""

            HomeScreen(
                viewModel = homeViewModel,
                userName = name,
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        // ===== Hồ sơ (YÊU CẦU ĐĂNG NHẬP) =====
        composable(Routes.PROFILE) {
            if (authManager.currentUser == null) {
                LaunchedEffect(Unit) { goToLogin() }
                return@composable
            }

            val homeViewModel: HomeViewModel = viewModel()
            val todos by homeViewModel.state.collectAsStateWithLifecycle()


            ProfileScreen(
                authManager = authManager,
                tasks = todos.tasks,
                onBack = { navController.popBackStack() },
                onLoggedOut = { goToLogin() }
            )
        }
    }
}