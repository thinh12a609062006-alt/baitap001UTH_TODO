package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val Blue = Color(0xFF2F5BEA)
private val Orange = Color(0xFFF59E0B)
private val Ink = Color(0xFF1B1F3B)
private val Soft = Color(0xFFF3F5FC)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userName: String,
    onProfileClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks = state.tasks
    val shownName = userName.ifBlank { "bạn" }

    // Thống kê tính từ dữ liệu API
    val total = tasks.size
    val done = tasks.count { it.done }
    val percent = if (total == 0) 0 else done * 100 / total

    val today = LocalDate.now()
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("vi"))
    val dateText = "${dayName.replaceFirstChar { it.uppercase() }}, ${today.dayOfMonth} tháng ${today.monthValue}"

    Scaffold(
        containerColor = Color.White,
        bottomBar = { HomeBottomBar(onAddClick = { /* Bước 8 sẽ nối vào đây */ }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ===== Header =====
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp).clip(CircleShape).background(Blue)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(shownName.take(1).uppercase(), color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(dateText, fontSize = 12.sp, color = Color.Gray)
                        Text("Chào $shownName!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                    }
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Soft),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Notifications, null, tint = Ink, modifier = Modifier.size(20.dp)) }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ===== Thẻ tiến độ =====
            item { ProgressCard(done, total, percent) }

            // ===== 2 thẻ danh mục =====
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryCard(
                        icon = Icons.Default.School, title = "Học tập",
                        remaining = tasks.count { it.category == "study" && !it.done },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        icon = Icons.Default.Home, title = "Cuộc sống",
                        remaining = tasks.count { it.category == "life" && !it.done },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ===== Tiêu đề danh sách =====
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Việc hôm nay", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = Ink, modifier = Modifier.weight(1f))
                    Text("Xem tất cả", fontSize = 12.sp, color = Blue, fontWeight = FontWeight.Medium)
                }
            }

            // ===== 4 trạng thái: Loading / Lỗi / Trống / Có dữ liệu =====
            when {
                state.isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue)
                    }
                }
                state.error != null -> item {
                    ErrorBox(message = state.error!!, onRetry = { viewModel.loadTasks() })
                }
                tasks.isEmpty() -> item {
                    Text(
                        "Chưa có công việc nào. Bấm nút + để thêm việc đầu tiên!",
                        color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
                else -> items(tasks, key = { it.id }) { task -> TaskItem(task) }
            }
        }
    }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFDECEC))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color(0xFFB42318), fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) { Text("Thử lại") }
    }
}

@Composable
private fun ProgressCard(done: Int, total: Int, percent: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Blue).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Tiến độ hôm nay", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(4.dp))
            Text("$done/$total việc", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text(
                when {
                    total == 0 -> "Chưa có việc nào"
                    percent == 100 -> "Tuyệt vời, xong hết rồi!"
                    percent >= 50 -> "Đã hơn nửa chặng, cố lên!"
                    else -> "Bắt đầu thôi nào!"
                },
                fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f)
            )
        }
        Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 6.dp.toPx()
                val arcSize = Size(size.width - stroke, size.height - stroke)
                val topLeft = Offset(stroke / 2, stroke / 2)
                drawArc(Color.White.copy(alpha = 0.3f), 0f, 360f, false, topLeft, arcSize, style = Stroke(stroke))
                drawArc(Color.White, -90f, 360f * percent / 100f, false, topLeft, arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round))
            }
            Text("$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun CategoryCard(icon: ImageVector, title: String, remaining: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Soft).padding(14.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = Blue, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.height(10.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("$remaining việc còn lại", fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
private fun TaskItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Soft)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Đã xong: vòng tròn xanh có dấu tích. Chưa xong: vòng tròn trắng
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (task.done) Blue else Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (task.done) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = if (task.done) Color.Gray else Ink,
                textDecoration = if (task.done) TextDecoration.LineThrough else null
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val chipColor = if (task.category == "life") Orange else Blue
                Text(
                    task.categoryLabel, fontSize = 11.sp, color = chipColor, fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(chipColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                if (task.time.isNotBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text(task.time, fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
    }
}

@Composable
private fun HomeBottomBar(onAddClick: () -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true, onClick = {},
            icon = { Icon(Icons.Default.Home, null) }, label = { Text("Trang chủ", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue, selectedTextColor = Blue, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.List, null) }, label = { Text("Danh sách", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false, onClick = onAddClick,
            icon = {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Blue),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Thông báo", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Cài đặt", fontSize = 10.sp) }
        )
    }
}