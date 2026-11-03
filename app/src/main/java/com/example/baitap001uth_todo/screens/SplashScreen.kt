package com.example.baitap001uth_todo.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baitap001uth_todo.model.AppInfo
import kotlinx.coroutines.delay
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

// Màu xanh chủ đạo của app (các màn sau sẽ dùng lại)
val UthBlue = Color(0xFF2F5BEA)

@Composable
fun SplashScreen(
    appInfo: AppInfo,
    onFinished: () -> Unit
) {
    // started = false lúc đầu, đổi thành true thì thanh tiến trình bắt đầu chạy
    var started by remember { mutableStateOf(false) }

    // progress tự chạy mượt từ 0f đến 1f trong 2 giây
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "splashProgress"
    )

    // Chạy một lần khi màn hình xuất hiện
    LaunchedEffect(Unit) {
        started = true
        delay(2300)      // đợi 2,3 giây
        onFinished()     // báo cho bên ngoài: "Splash xong rồi"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UthBlue)
    ) {
        // Phần giữa: logo + tên + khẩu hiệu
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Logo",
                    tint = UthBlue,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = appInfo.name,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = appInfo.tagline,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }

        // Phần đáy: thanh tiến trình + phiên bản
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.width(120.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Text(
                text = appInfo.version,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}
@Composable
fun UthLogo() {
    val lightBlue = Color(0xFFA9B8F5)

    // Ô trắng bo góc
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Hai dòng xếp dọc
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Dòng 1: vòng tròn xanh có dấu tick + thanh xanh đậm
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(UthBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(UthBlue)
                )
            }

            // Dòng 2: vòng tròn rỗng + thanh xanh nhạt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(2.dp, lightBlue, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(lightBlue)
                )
            }
        }
    }
}