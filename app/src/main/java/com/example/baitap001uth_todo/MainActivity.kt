package com.example.baitap001uth_todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.baitap001uth_todo.navigation.AppNavigation
import com.example.baitap001uth_todo.ui.theme.Baitap001UTH_TODOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Baitap001UTH_TODOTheme {
                AppNavigation()
            }
        }
    }
}