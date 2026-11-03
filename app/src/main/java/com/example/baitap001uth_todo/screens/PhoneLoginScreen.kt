package com.example.baitap001uth_todo.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baitap001uth_todo.data.AuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

private fun Context.findActivity(): Activity? {
    var c = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

@Composable
fun PhoneLoginScreen(authManager: AuthManager, onBack: () -> Unit, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
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
        Text("Đăng nhập bằng số điện thoại", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1F3B))
        Spacer(Modifier.height(24.dp))

        if (verificationId == null) {
            OutlinedTextField(
                value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại (vd 0912345678)") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val activity = context.findActivity() ?: return@Button
                    loading = true
                    authManager.sendPhoneCode(
                        activity, phone,
                        onSent = { id ->
                            loading = false
                            verificationId = id
                            Toast.makeText(context, "Đã gửi mã xác minh", Toast.LENGTH_SHORT).show()
                        },
                        onAuto = { cred -> scope.launch { handleResult(authManager.signInWithCredential(cred)) } },
                        onFail = { msg ->
                            loading = false
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !loading && phone.length >= 9,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F5BEA))
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Gửi mã")
            }
        } else {
            Text("Nhập mã đã gửi tới $phone", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = code, onValueChange = { code = it.take(6) }, label = { Text("Mã xác minh") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    loading = true
                    scope.launch { handleResult(authManager.signInWithPhoneCode(verificationId!!, code)) }
                },
                enabled = !loading && code.length == 6,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F5BEA))
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Xác nhận")
            }
            TextButton(onClick = { verificationId = null; code = "" }, modifier = Modifier.fillMaxWidth()) {
                Text("Đổi số điện thoại", color = Color(0xFF2F5BEA))
            }
        }
    }
}