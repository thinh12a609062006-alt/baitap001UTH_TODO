package com.example.baitap001uth_todo.screens

import androidx.compose.runtime.Composable

@Composable
fun rememberFacebookLogin(onToken: (String) -> Unit, onError: (String) -> Unit): () -> Unit {
    return { onError("Facebook chưa được cấu hình") }
}
