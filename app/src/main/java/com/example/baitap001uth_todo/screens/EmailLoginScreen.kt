package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baitap001uth_todo.data.AuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun EmailLoginScreen(authManager: AuthManager, onBack: () -> Unit, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    fun handleResult(result: Result<FirebaseUser>) {
        loading = false
        result.onSuccess {
            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }.onFailure { Toast.makeText(context, it.message ?: "Đăng nhập thất bại", Toast.LENGTH_LONG).show() }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).systemBarsPadding().padding(24.dp)
    ) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại") }
        Spacer(Modifier.height(16.dp))
        Text(if (isRegister) "Tạo tài khoản" else "Đăng nhập bằng Email",
            fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1F3B))
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Mật khẩu (tối thiểu 6 ký tự)") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                loading = true
                scope.launch {
                    handleResult(
                        if (isRegister) authManager.registerWithEmail(email, password)
                        else authManager.signInWithEmail(email, password)
                    )
                }
            },
            enabled = !loading && email.isNotBlank() && password.length >= 6,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F5BEA))
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            else Text(if (isRegister) "Đăng ký" else "Đăng nhập")
        }

        TextButton(onClick = { isRegister = !isRegister }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isRegister) "Đã có tài khoản? Đăng nhập" else "Chưa có tài khoản? Đăng ký", color = Color(0xFF2F5BEA))
        }
    }
}