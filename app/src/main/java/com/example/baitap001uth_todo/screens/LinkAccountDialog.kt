package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.baitap001uth_todo.data.AuthManager
import kotlinx.coroutines.launch

@Composable
fun LinkAccountDialog(
    authManager: AuthManager,
    onDismiss: () -> Unit,
    onLinked: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Liên kết tài khoản", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Liên kết Email để giữ lại công việc khi đăng xuất hoặc đổi máy.")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Mật khẩu (tối thiểu 6 ký tự)") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    loading = true
                    scope.launch {
                        authManager.linkWithEmail(email, password)
                            .onSuccess {
                                Toast.makeText(context, "Liên kết thành công!", Toast.LENGTH_SHORT).show()
                                onLinked()
                            }
                            .onFailure {
                                loading = false
                                Toast.makeText(context, it.message ?: "Liên kết thất bại", Toast.LENGTH_LONG).show()
                            }
                    }
                },
                enabled = !loading && email.isNotBlank() && password.length >= 6
            ) { Text(if (loading) "Đang xử lý..." else "Liên kết") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !loading) { Text("Huỷ") } }
    )
}