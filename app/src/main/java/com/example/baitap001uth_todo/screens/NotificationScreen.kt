package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baitap001uth_todo.model.AppNotification
import com.example.baitap001uth_todo.model.NotificationType
import com.example.baitap001uth_todo.viewmodel.HomeViewModel
import com.example.baitap001uth_todo.viewmodel.NotificationViewModel
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Warning


private val Red = Color(0xFFE5484D)
private val Green = Color(0xFF1E9E5A)

@Composable
fun NotificationScreen(
    notificationViewModel: NotificationViewModel,
    homeViewModel: HomeViewModel,
    onHomeClick: () -> Unit,
    onListClick: () -> Unit,
    onReminderSettingsClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val items by notificationViewModel.items.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(homeState.isLoading) {
        if (!homeState.isLoading) notificationViewModel.start(homeState.tasks)
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            AppBottomBar(
                selected = BottomTab.NOTIFICATIONS,
                onHomeClick = onHomeClick,
                onListClick = onListClick,
                onNotificationsClick = {},
                onAddClick = onAddTaskClick,
                onSettingsClick = onSettingsClick,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Thông báo", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = Ink, modifier = Modifier.weight(1f))
                if (items.any { it.unread }) {
                    TextButton(onClick = { notificationViewModel.markAllRead() }) {
                        Text("Đánh dấu đã đọc", fontSize = 12.sp, color = Blue)
                    }
                }
            }

            if (items.isEmpty()) {
                EmptyView(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    onAddClick = onAddTaskClick,
                    onSettingsClick = onReminderSettingsClick
                )
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Panel)
                    ) {
                        items.forEachIndexed { index, item ->
                            NotificationRow(item)
                            if (index < items.lastIndex) HorizontalDivider(color = Line)
                        }
                    }
                }
            }
        }
    }
    if (showAdd) {
        AddTaskDialog(
            isSaving = homeState.isSaving,
            onDismiss = { showAdd = false },
            onSave = { request ->
                homeViewModel.addTask(request) { result ->
                    result
                        .onSuccess {
                            Toast.makeText(context, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show()
                            showAdd = false
                        }
                        .onFailure {
                            Toast.makeText(context, "Thêm thất bại: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
        )
    }
}

@Composable
private fun NotificationRow(n: AppNotification) {
    val (icon, color) = when (n.type) {
        NotificationType.DUE_SOON -> Icons.Outlined.AccessAlarm to Blue
        NotificationType.REMINDER -> Icons.Outlined.AccessAlarm to Blue
        NotificationType.OVERDUE -> Icons.Outlined.Warning to Red
        NotificationType.GOOD_JOB -> Icons.Outlined.Check to Green
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(n.title, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = Ink, modifier = Modifier.weight(1f))
                Text(n.timeLabel, fontSize = 11.sp, color = Color.Gray)
                if (n.unread) {
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Blue))
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(n.message, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun EmptyView(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier.size(96.dp).clip(CircleShape).background(Blue.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Panel),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Notifications, null, tint = Blue, modifier = Modifier.size(34.dp)) }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Green),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
        }

        Spacer(Modifier.height(20.dp))
        Text("Chưa có thông báo nào", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text(
            "Khi có việc sắp đến hạn, quá hạn hoặc lời nhắc, bạn sẽ thấy chúng ở đây.",
            fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            modifier = Modifier.height(48.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Thêm việc cần làm", fontWeight = FontWeight.Medium)
        }
        TextButton(onClick = onSettingsClick) {
            Text("Cài đặt nhắc việc", color = Blue, fontWeight = FontWeight.Medium)
        }
    }
}