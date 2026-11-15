package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BottomTab { HOME, LIST, NOTIFICATIONS }


@Composable
fun AppBottomBar(
    selected: BottomTab,
    onHomeClick: () -> Unit,
    onListClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val blue = Color(0xFF2F5BEA)
    val tabColors = NavigationBarItemDefaults.colors(
        selectedIconColor = blue,
        selectedTextColor = blue,
        indicatorColor = Color.Transparent
    )

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = selected == BottomTab.HOME, onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Trang chủ", fontSize = 10.sp) },
            colors = tabColors
        )
        NavigationBarItem(
            selected = selected == BottomTab.LIST, onClick = onListClick,
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
            label = { Text("Danh sách", fontSize = 10.sp) },
            colors = tabColors
        )
        NavigationBarItem(
            selected = false, onClick = onAddClick,
            icon = {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(blue),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selected == BottomTab.NOTIFICATIONS, onClick = onNotificationsClick,
            icon = { Icon(Icons.Default.Notifications, null) },
            label = { Text("Thông báo", fontSize = 10.sp) },
            colors = tabColors
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Cài đặt", fontSize = 10.sp) }
        )
    }
}