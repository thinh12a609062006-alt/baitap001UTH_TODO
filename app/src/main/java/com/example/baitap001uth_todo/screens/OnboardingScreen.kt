package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baitap001uth_todo.model.OnboardingPage
import androidx.compose.foundation.border
import androidx.compose.ui.unit.Dp

@Composable
fun OnboardingScreen(
    page: OnboardingPage,        // object dữ liệu của trang này
    totalPages: Int,             // tổng số trang, để vẽ dấu chấm
    onNext: () -> Unit,          // bấm nút chính
    onSkip: () -> Unit           // bấm "Bỏ qua"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Hàng trên cùng: logo nhỏ bên trái, "Bỏ qua" bên phải
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SmallBrand()
            TextButton(onClick = onSkip) {
                Text("Bỏ qua", color = Color.Gray, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Khung minh họa (đổi theo từng trang)
        Illustration(pageId = page.id)

        // Đẩy phần chữ xuống dưới
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1F3B)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = page.description,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))
        PageIndicator(total = totalPages, current = page.id)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UthBlue)
        ) {
            Text(
                text = page.buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Logo nhỏ + chữ "UTH Todo" ở góc trên trái
@Composable
private fun SmallBrand() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(UthBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("UTH") }
                append(" Todo")
            },
            color = UthBlue,
            fontSize = 16.sp
        )
    }
}

// Hàng dấu chấm: trang hiện tại dài hơn và màu xanh
@Composable
private fun PageIndicator(total: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..total) {
            val isCurrent = i == current
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (isCurrent) 24.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isCurrent) UthBlue else Color(0xFFD5DBEA))
            )
        }
    }
}

// Chọn icon minh họa theo số trang
private fun iconForPage(id: Int): ImageVector = when (id) {
    1 -> Icons.Default.CheckCircle
    2 -> Icons.Default.DateRange
    else -> Icons.Default.Notifications
}
// Khung xanh nhạt, bên trong đổi theo từng trang
@Composable
private fun Illustration(pageId: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEAEFFC)),
        contentAlignment = Alignment.Center
    ) {
        when (pageId) {
            1 -> TaskListIllustration()
            2 -> CategoryIllustration()
            else -> ReminderIllustration()
        }
    }
}

// ----- Trang 1: danh sách việc -----
@Composable
private fun TaskListIllustration() {
    Column(
        modifier = Modifier.width(220.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TaskRow(done = true, barWidth = 110.dp)
        TaskRow(done = false, barWidth = 140.dp)
        TaskRow(done = false, barWidth = 90.dp)
    }
}

@Composable
private fun TaskRow(done: Boolean, barWidth: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (done) {
            // Vòng tròn xanh có dấu tick
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(UthBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            // Vòng tròn rỗng
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFC5CDE8), CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (done) UthBlue else Color(0xFFD5DBEA))
        )
    }
}

// ----- Trang 2: hai nhóm Học tập / Cuộc sống -----
@Composable
private fun CategoryIllustration() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CategoryCard(label = "Học tập", color = UthBlue)
        CategoryCard(label = "Cuộc sống", color = Color(0xFFF59E0B))
    }
}

@Composable
private fun CategoryCard(label: String, color: Color) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFD5DBEA))
        )
    }
}

// ----- Trang 3: chuông và thông báo -----
@Composable
private fun ReminderIllustration() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = UthBlue,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nộp bài tập lúc 19:00",
            fontSize = 12.sp,
            color = Color(0xFF1B1F3B),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}