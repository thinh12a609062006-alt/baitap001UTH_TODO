package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(onBack: () -> Unit) {
    val blue = Blue
    val ink = Ink
    var enabled by rememberSaveable { mutableStateOf(true) }
    var overdueAlert by rememberSaveable { mutableStateOf(true) }
    var leadMinutes by rememberSaveable { mutableIntStateOf(15) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = blue, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Quay lại", color = blue, fontSize = 15.sp)
        }

        Spacer(Modifier.height(16.dp))
        Text("Cài đặt nhắc việc", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ink)
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel).padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Bật nhắc việc", fontSize = 15.sp, color = ink, modifier = Modifier.weight(1f))
                Switch(checked = enabled, onCheckedChange = { enabled = it })
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Báo việc quá hạn", fontSize = 15.sp, color = ink, modifier = Modifier.weight(1f))
                Switch(checked = overdueAlert, onCheckedChange = { overdueAlert = it }, enabled = enabled)
            }
            Spacer(Modifier.height(16.dp))
            Text("Nhắc trước khi đến hạn", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 15, 30, 60).forEach { m ->
                    FilterChip(
                        selected = leadMinutes == m,
                        onClick = { leadMinutes = m },
                        enabled = enabled,
                        label = { Text("$m phút") }
                    )
                }
            }
        }
    }
}