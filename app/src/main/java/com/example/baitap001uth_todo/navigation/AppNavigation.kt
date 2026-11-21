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
import com.example.baitap001uth_todo.screens.ListScreen
import com.example.baitap001uth_todo.screens.LoginScreen
import com.example.baitap001uth_todo.screens.OnboardingScreen
import com.example.baitap001uth_todo.screens.PhoneLoginScreen
import com.example.baitap001uth_todo.screens.ProfileScreen
import com.example.baitap001uth_todo.screens.SplashScreen
import com.example.baitap001uth_todo.viewmodel.HomeViewModel
import com.example.baitap001uth_todo.screens.DetailScreen
import com.example.baitap001uth_todo.screens.NotificationScreen
import com.example.baitap001uth_todo.screens.ReminderSettingsScreen
import com.example.baitap001uth_todo.viewmodel.NotificationViewModel
import com.example.baitap001uth_todo.screens.AddTaskScreen
import com.example.baitap001uth_todo.viewmodel.AddTaskViewModel
import com.example.baitap001uth_todo.screens.SettingsScreen
import com.example.baitap001uth_todo.viewmodel.SettingsViewModel
import com.example.baitap001uth_todo.screens.UthAppTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authManager = remember { AuthManager() }
    val repository = remember { OnboardingRepository() }

    val homeViewModel: HomeViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val settings by settingsViewModel.state.collectAsStateWithLifecycle()
    val notificationViewModel: NotificationViewModel = viewModel()

    val appInfo = AppInfo(
        name = "UTH Todo",
        tagline = "Việc học, việc nhà – gọn gàng mỗi ngày",
        version = "Phiên bản 1.0"
    )

    val goToLogin = {
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val goTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }
    val goAdd = {
        navController.navigate(Routes.ADD) { launchSingleTop = true }
    }
    UthAppTheme(settings) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH
        ) {
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

            composable(
                route = Routes.ONBOARDING,
                arguments = listOf(navArgument("pageId") { type = NavType.IntType })
            ) { backStackEntry ->
                val pageId = backStackEntry.arguments?.getInt("pageId") ?: 1
                val page = repository.getPage(pageId - 1)
                val total = repository.getTotalPages()

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

            composable(Routes.HOME) {
                val user = authManager.currentUser
                if (user == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                val name = user.displayName?.takeIf { it.isNotBlank() }
                    ?: user.email?.substringBefore("@")
                    ?: ""

                HomeScreen(
                    viewModel = homeViewModel,
                    userName = name,
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onListClick = { goTab(Routes.LIST) },
                    onTaskClick = { id -> navController.navigate(Routes.detail(id)) },
                    onNotificationsClick = { goTab(Routes.NOTIFICATIONS) },
                    onAddTaskClick = goAdd,
                    onSettingsClick = { goTab(Routes.SETTINGS) }
                )
            }

            composable(Routes.LIST) {
                if (authManager.currentUser == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                ListScreen(
                    viewModel = homeViewModel,
                    onHomeClick = { goTab(Routes.HOME) },
                    onTaskClick = { id -> navController.navigate(Routes.detail(id)) },
                    onNotificationsClick = { goTab(Routes.NOTIFICATIONS) },
                    onAddTaskClick = goAdd,
                    onSettingsClick = { goTab(Routes.SETTINGS) }
                )
            }
            composable(Routes.ADD) {
                if (authManager.currentUser == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                val addViewModel: AddTaskViewModel = viewModel()
                AddTaskScreen(
                    addViewModel = addViewModel,
                    homeViewModel = homeViewModel,
                    onClose = { navController.popBackStack() }
                )
            }
            composable(Routes.NOTIFICATIONS) {
                if (authManager.currentUser == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                NotificationScreen(
                    notificationViewModel = notificationViewModel,
                    homeViewModel = homeViewModel,
                    onHomeClick = { goTab(Routes.HOME) },
                    onListClick = { goTab(Routes.LIST) },
                    onReminderSettingsClick = { navController.navigate(Routes.REMINDER_SETTINGS) },
                    onAddTaskClick = goAdd,
                    onSettingsClick = { goTab(Routes.SETTINGS) }
                )
            }
            composable(Routes.SETTINGS) {
                val user = authManager.currentUser
                if (user == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                val name = user.displayName?.takeIf { it.isNotBlank() }
                    ?: user.email?.substringBefore("@")
                    ?: ""

                SettingsScreen(
                    viewModel = settingsViewModel,
                    userName = name,
                    onHomeClick = { goTab(Routes.HOME) },
                    onListClick = { goTab(Routes.LIST) },
                    onNotificationsClick = { goTab(Routes.NOTIFICATIONS) },
                    onAddTaskClick = goAdd,
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                )
            }
            composable(Routes.REMINDER_SETTINGS) {
                ReminderSettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                if (authManager.currentUser == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                DetailScreen(
                    taskId = taskId,
                    viewModel = homeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PROFILE) {
                if (authManager.currentUser == null) {
                    LaunchedEffect(Unit) { goToLogin() }
                    return@composable
                }
                val state by homeViewModel.state.collectAsStateWithLifecycle()

                ProfileScreen(
                    authManager = authManager,
                    tasks = state.tasks,
                    onBack = { navController.popBackStack() },
                    onLoggedOut = { goToLogin() }
                )
            }
        }
    }
}