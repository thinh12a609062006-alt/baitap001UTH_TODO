package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/** Logo Google: vòng cung 4 màu (đỏ, vàng, xanh lá, xanh dương) + thanh ngang. */
@Composable
fun GoogleLogo(size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = this.size.minDimension * 0.2f
        val inset = stroke / 2
        val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
        val topLeft = Offset(inset, inset)
        // Các cung vẽ theo chiều kim đồng hồ, bắt đầu từ bên phải (0 độ)
        drawArc(Color(0xFF4285F4), -10f, 60f, false, topLeft, arcSize, style = Stroke(stroke))   // xanh dương
        drawArc(Color(0xFF34A853), 50f, 90f, false, topLeft, arcSize, style = Stroke(stroke))    // xanh lá
        drawArc(Color(0xFFFBBC05), 140f, 70f, false, topLeft, arcSize, style = Stroke(stroke))   // vàng
        drawArc(Color(0xFFEA4335), 210f, 100f, false, topLeft, arcSize, style = Stroke(stroke))  // đỏ
        // Thanh ngang màu xanh dương của chữ G
        drawRect(
            Color(0xFF4285F4),
            topLeft = Offset(this.size.width * 0.5f, this.size.height * 0.42f),
            size = Size(this.size.width * 0.45f, stroke * 0.9f)
        )
    }
}

/** Logo Facebook: hình tròn xanh với chữ f trắng. */
@Composable
fun FacebookLogo(size: Dp = 22.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(Color(0xFF1877F2)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text("f", color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.95f).sp)
    }
}

/** Icon Email: phong bì màu đỏ. */
@Composable
fun EmailLogo(size: Dp = 22.dp) {
    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(size))
}

/** Icon Điện thoại: màu xanh lá. */
@Composable
fun PhoneLogo(size: Dp = 22.dp) {
    Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF34A853), modifier = Modifier.size(size))
}
/** Logo Gmail: chữ M nhiều màu giống logo thật. */
@Composable
fun GmailLogo(size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Toạ độ khung chữ M (chiếm khoảng 90% chiều rộng, 75% chiều cao)
        val left = w * 0.05f
        val right = w * 0.95f
        val top = h * 0.17f
        val bottom = h * 0.83f
        val sideW = w * 0.18f          // bề rộng 2 cột bên
        val midX = w * 0.5f
        val vY = h * 0.60f             // đáy của chữ V ở giữa

        fun poly(color: Color, pts: List<Offset>) {
            val p = Path().apply {
                moveTo(pts[0].x, pts[0].y)
                for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
                close()
            }
            drawPath(p, color)
        }

        // Cột trái màu xanh dương
        // Cột trái màu xanh dương
        poly(Color(0xFF4285F4), listOf(
            Offset(left, top + h * 0.12f), Offset(left + sideW, top + h * 0.30f),
            Offset(left + sideW, bottom), Offset(left, bottom)))

        // Cột phải màu xanh lá
        poly(Color(0xFF34A853), listOf(
            Offset(right, top + h * 0.12f), Offset(right - sideW, top + h * 0.30f),
            Offset(right - sideW, bottom), Offset(right, bottom)))

        // Góc trái trên màu đỏ đậm
        poly(Color(0xFFC5221F), listOf(
            Offset(left, top + h * 0.12f), Offset(left, top),
            Offset(left + sideW, top), Offset(left + sideW, top + h * 0.30f)))

        // Góc phải trên màu vàng
        poly(Color(0xFFFBBC04), listOf(
            Offset(right, top + h * 0.12f), Offset(right, top),
            Offset(right - sideW, top), Offset(right - sideW, top + h * 0.30f)))

        // Chữ V ở giữa màu đỏ
        poly(Color(0xFFEA4335), listOf(
            Offset(left + sideW, top), Offset(midX, vY),
            Offset(right - sideW, top), Offset(right - sideW, top + h * 0.30f),
            Offset(midX, vY + h * 0.10f), Offset(left + sideW, top + h * 0.30f)))
    }
}