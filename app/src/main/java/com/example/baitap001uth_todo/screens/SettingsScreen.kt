package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baitap001uth_todo.viewmodel.AccentOption
import com.example.baitap001uth_todo.viewmodel.SettingsViewModel
import com.example.baitap001uth_todo.viewmodel.ThemeMode

private val SwitchGreen = Color(0xFF34C759)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    userName: String,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onListClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAddTaskClick: () -> Unit
) {
    val s by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            AppBottomBar(
                selected = BottomTab.SETTINGS,
                onHomeClick = onHomeClick,
                onListClick = onListClick,
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = {},
                onAddClick = onAddTaskClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cài đặt", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = Ink, modifier = Modifier.weight(1f))
                Avatar(
                    name = userName.ifBlank { "Khách" }, size = 40,
                    modifier = Modifier.clickable { onProfileClick() }
                )
            }
            SectionLabel("GIAO DIỆN")
            Section {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeCard("Sáng", ThemeMode.LIGHT, s.themeMode == ThemeMode.LIGHT, Modifier.weight(1f)) {
                        viewModel.onThemeMode(ThemeMode.LIGHT)
                    }
                    ThemeCard("Tối", ThemeMode.DARK, s.themeMode == ThemeMode.DARK, Modifier.weight(1f)) {
                        viewModel.onThemeMode(ThemeMode.DARK)
                    }
                    ThemeCard("Tự động", ThemeMode.AUTO, s.themeMode == ThemeMode.AUTO, Modifier.weight(1f)) {
                        viewModel.onThemeMode(ThemeMode.AUTO)
                    }
                }
                HorizontalDivider(color = Line)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Màu chủ đạo", fontSize = 15.sp, color = Ink, modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AccentOption.values().forEach { option ->
                            AccentDot(option.color(), s.accent == option) { viewModel.onAccent(option) }
                        }
                    }
                }
                HorizontalDivider(color = Line)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cỡ chữ", fontSize = 15.sp, color = Ink)
                    Spacer(Modifier.width(20.dp))
                    Text("A", fontSize = 12.sp, color = Color.Gray)
                    Slider(
                        value = s.fontSize,
                        onValueChange = viewModel::onFontSize,
                        colors = SliderDefaults.colors(
                            thumbColor = Blue,
                            activeTrackColor = Blue,
                            inactiveTrackColor = Line
                        ),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    )
                    Text("A", fontSize = 20.sp, color = Ink)
                }
            }
            SectionLabel("THÔNG BÁO")
            Section {
                SwitchRow("Nhắc việc", "Báo trước hạn chót 15 phút", s.remind, viewModel::onRemind)
                HorizontalDivider(color = Line)
                SwitchRow("Âm thanh", "Phát âm khi có thông báo", s.sound, viewModel::onSound)
                HorizontalDivider(color = Line)
                SwitchRow("Tổng kết cuối ngày", "Gửi lúc 21:00 mỗi tối", s.dailySummary, viewModel::onDailySummary)
            }
            SectionLabel("CHUNG")
            Section {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ngôn ngữ", fontSize = 15.sp, color = Ink, modifier = Modifier.weight(1f))
                    Text(s.language, fontSize = 15.sp, color = Color.Gray)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray,
        modifier = Modifier.padding(top = 22.dp, bottom = 8.dp, start = 4.dp))
}

@Composable
private fun Section(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel),
        content = content
    )
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, color = Ink)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SwitchGreen, checkedThumbColor = Color.White,
                uncheckedTrackColor = Line, uncheckedThumbColor = Color.White,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
@Composable
private fun AccentDot(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .then(if (selected) Modifier.border(2.dp, color, CircleShape) else Modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(26.dp).clip(CircleShape).background(color))
    }
}
@Composable
private fun ThemeCard(
    label: String, mode: ThemeMode, selected: Boolean,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth().height(76.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, if (selected) Blue else Line, RoundedCornerShape(12.dp))
        ) {
            when (mode) {
                ThemeMode.LIGHT -> MiniScreen(light = true)
                ThemeMode.DARK -> MiniScreen(light = false)
                ThemeMode.AUTO -> Row(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f).fillMaxHeight()) { MiniScreen(light = true) }
                    Box(Modifier.weight(1f).fillMaxHeight()) { MiniScreen(light = false) }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label, fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Blue else Ink
        )
    }
}

@Composable
private fun MiniScreen(light: Boolean) {
    val screen = if (light) Color(0xFFF1F3FA) else Color(0xFF0B0B10)
    val title = if (light) Color(0xFFB8BECF) else Color(0xFF3A3A44)
    val card = if (light) Color.White else Color(0xFF26262E)
    Column(
        modifier = Modifier.fillMaxSize().background(screen).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(Modifier.width(24.dp).height(5.dp).clip(RoundedCornerShape(3.dp)).background(title))
        Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(4.dp)).background(card))
        Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(4.dp)).background(card))
    }
}