package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.baitap001uth_todo.model.Todo
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

private val Blue = Color(0xFF2F5BEA)
private val Ink = Color(0xFF1B1F3B)

@Composable
fun ProfileScreen(
    authManager: AuthManager,
    todos: List<Todo>,
    onBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }
    var showLink by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    val user = remember(version) { authManager.currentUser }

    val isAnonymous = user?.isAnonymous == true
    val isGoogle = user?.providerData?.any { it.providerId == "google.com" } == true
    val isFacebook = user?.providerData?.any { it.providerId == "facebook.com" } == true
    val name = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@")
        ?: "Khách"
    val email = user?.email?.takeIf { it.isNotBlank() }
        ?: user?.phoneNumber?.takeIf { it.isNotBlank() }
        ?: "Chưa có email"

    val total = todos.size
    val done = todos.count { it.isCompleted }
    val percent = if (total == 0) 0 else done * 100 / total

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart).clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Blue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Cài đặt", color = Blue, fontSize = 14.sp)
            }
            Text("Hồ sơ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
        }

        Spacer(Modifier.height(24.dp))
        Avatar(name = name, size = 80)
        Spacer(Modifier.height(12.dp))
        Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(email, fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        val badge = when {
            isGoogle -> "✓ Đã liên kết tài khoản Google" to Color(0xFF1E9E5A)
            isFacebook -> "✓ Đã liên kết tài khoản Facebook" to Color(0xFF1877F2)
            isAnonymous -> "Tài khoản dùng thử" to Color.Gray
            else -> "✓ Đã đăng nhập" to Color(0xFF1E9E5A)
        }
        Text(
            badge.first, fontSize = 11.sp, color = badge.second, fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(badge.second.copy(alpha = 0.12f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("$done", "Việc đã xong", Modifier.weight(1f))
            StatCard("${total - done}", "Đang chờ", Modifier.weight(1f))
            StatCard("$percent%", "Hoàn thành", Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))
        WeekChart(todos)

        Spacer(Modifier.weight(1f))
        if (isAnonymous) {
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = { showLink = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Liên kết tài khoản để giữ dữ liệu", color = Blue) }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .clickable { showConfirm = true }
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Blue, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Đăng xuất", color = Blue, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }


    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Đăng xuất", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (isAnonymous)
                        "Đây là tài khoản dùng thử. Đăng xuất sẽ MẤT toàn bộ công việc. Hãy liên kết tài khoản trước nếu muốn giữ lại."
                    else
                        "Bạn có chắc muốn đăng xuất khỏi tài khoản này không?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    scope.launch {
                        authManager.signOut(context)
                        onLoggedOut()
                    }
                }) { Text("Đăng xuất", color = Color(0xFFE5484D)) }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Huỷ") } }
        )
    }
    if (showLink) {
        LinkAccountDialog(
            authManager = authManager,
            onDismiss = { showLink = false },
            onLinked = { showLink = false; version++ }
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(Color.White).padding(14.dp)
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}


@Composable
private fun WeekChart(todos: List<Todo>) {
    val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val today = LocalDate.now()
    val monday = today.with(DayOfWeek.MONDAY)
    val values = List(7) { i ->
        val day = monday.plusDays(i.toLong()).toString()
        todos.count { it.isCompleted && it.date == day }
    }
    val todayIndex = today.dayOfWeek.value - 1
    val maxValue = maxOf(values.max(), 1)

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tuần này", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink,
                modifier = Modifier.weight(1f))
            Text("Việc xong mỗi ngày", fontSize = 11.sp, color = Color.Gray)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(110.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            labels.forEachIndexed { i, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val barHeight = if (values[i] == 0) 3.dp else (70.dp * values[i] / maxValue)
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    values[i] == 0 -> Color(0xFFE3E6F0)
                                    i == todayIndex -> Blue
                                    else -> Color(0xFFB7C6F8)
                                }
                            )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        label, fontSize = 10.sp,
                        color = if (i == todayIndex) Ink else Color.Gray,
                        fontWeight = if (i == todayIndex) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}


@Composable
fun Avatar(name: String, size: Int, modifier: Modifier = Modifier) {
    val initials = name.trim().split(" ").filter { it.isNotEmpty() }
        .takeLast(2).joinToString("") { it.first().uppercase() }.ifEmpty { "?" }
    Box(
        modifier = modifier.size(size.dp).clip(CircleShape).background(Color(0xFFE3E9FF)),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = Blue, fontWeight = FontWeight.Bold,
            fontSize = (size / 3).sp, textAlign = TextAlign.Center)
    }
}