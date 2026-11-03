package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baitap001uth_todo.data.AuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authManager: AuthManager,
    onEmailClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    // Lỗi -> Toast báo lỗi. Thành công -> Toast thành công + sang Home.
    fun handleResult(result: Result<FirebaseUser>) {
        loading = false
        result.onSuccess { user ->
            Toast.makeText(context, "Đăng nhập thành công! Chào ${user.displayName ?: "bạn"}",
                Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }.onFailure { e ->
            Toast.makeText(context, e.message ?: "Đăng nhập thất bại", Toast.LENGTH_LONG).show()
        }
    }

    val startFacebook = rememberFacebookLogin(
        onToken = { token -> scope.launch { handleResult(authManager.signInWithFacebookToken(token)) } },
        onError = { msg ->
            loading = false
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF2F5BEA)),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Checklist, contentDescription = null, tint = Color.White) }
        Spacer(Modifier.height(20.dp))
        Text("Chào mừng đến với\nUTH Todo", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1F3B))
        Spacer(Modifier.height(8.dp))
        Text("Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.", fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(40.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LoginButton("Tiếp tục với Google", { GoogleLogo() }) {
                loading = true
                scope.launch { handleResult(authManager.signInWithGoogle(context)) }
            }
            LoginButton("Tiếp tục với Facebook", { FacebookLogo() }) {
                loading = true
                startFacebook()
            }
            LoginButton("Tiếp tục với Email", { GmailLogo() }, onEmailClick)
            LoginButton("Tiếp tục với Số điện thoại", { PhoneLogo() }, onPhoneClick)

            Text("hoặc", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

            TextButton(
                onClick = {
                    loading = true
                    scope.launch { handleResult(authManager.signInAnonymously()) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dùng thử không cần tài khoản", color = Color(0xFF2F5BEA), fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun LoginButton(text: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFDDE1F0))
    ) {
        icon()
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color(0xFF1B1F3B), fontWeight = FontWeight.Medium)
    }
    Spacer(Modifier.height(10.dp))
}