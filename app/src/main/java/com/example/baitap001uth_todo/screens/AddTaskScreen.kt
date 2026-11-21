package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baitap001uth_todo.viewmodel.AddTaskViewModel
import com.example.baitap001uth_todo.viewmodel.DateChoice
import com.example.baitap001uth_todo.viewmodel.HomeViewModel
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    addViewModel: AddTaskViewModel,
    homeViewModel: HomeViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val form by addViewModel.state.collectAsStateWithLifecycle()
    val home by homeViewModel.state.collectAsStateWithLifecycle()
    var showTimePicker by remember { mutableStateOf(false) }

    val canSave = form.canSave && !home.isSaving
    fun save() {
        val request = addViewModel.buildRequest() ?: return
        homeViewModel.addTask(request) { result ->
            result
                .onSuccess {
                    Toast.makeText(context, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show()
                    onClose()
                }
                .onFailure {
                    Toast.makeText(context, "Thêm thất bại: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .systemBarsPadding()
            .imePadding()
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("Hủy", color = Blue, fontSize = 15.sp,
                modifier = Modifier.align(Alignment.CenterStart)
                    .clickable(enabled = !home.isSaving) { onClose() })
            Text("Việc mới", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink,
                modifier = Modifier.align(Alignment.Center))
            Text("Lưu", fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = if (canSave) Blue else Color.Gray,
                modifier = Modifier.align(Alignment.CenterEnd)
                    .clickable(enabled = canSave) { save() })
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel)) {
                Text("Tên công việc", fontSize = 12.sp, color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp))
                TextField(
                    value = form.title, onValueChange = addViewModel::onTitle,
                    placeholder = { Text("Ví dụ: Ôn chương 4 – Giải thuật", color = Color.Gray) },
                    singleLine = true, colors = plainFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(color = Soft)
                Text("Ghi chú", fontSize = 12.sp, color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp))
                TextField(
                    value = form.note, onValueChange = addViewModel::onNote,
                    placeholder = { Text("Thêm ghi chú (không bắt buộc)", color = Color.Gray) },
                    minLines = 2, colors = plainFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Label("Nhóm")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChoiceBox("Học tập", form.category == "study",
                    Modifier.weight(1f).height(52.dp)) { addViewModel.onCategory("study") }
                ChoiceBox("Cuộc sống", form.category == "life",
                    Modifier.weight(1f).height(52.dp)) { addViewModel.onCategory("life") }
            }
            Label("Ngày")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChoiceBox("Hôm nay", form.dateChoice == DateChoice.TODAY,
                    Modifier.weight(1f).height(44.dp)) { addViewModel.onDateChoice(DateChoice.TODAY) }
                ChoiceBox("Ngày mai", form.dateChoice == DateChoice.TOMORROW,
                    Modifier.weight(1f).height(44.dp)) { addViewModel.onDateChoice(DateChoice.TOMORROW) }
                ChoiceBox("Tuần này", form.dateChoice == DateChoice.THIS_WEEK,
                    Modifier.weight(1f).height(44.dp)) { addViewModel.onDateChoice(DateChoice.THIS_WEEK) }
            }
            Text(
                "Hạn: ${form.resolvedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(Modifier.height(14.dp))
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true }.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Schedule, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Giờ", fontSize = 15.sp, color = Ink, modifier = Modifier.weight(1f))
                    Text(form.time, fontSize = 15.sp, color = Ink,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Soft)
                            .padding(horizontal = 12.dp, vertical = 6.dp))
                }
                HorizontalDivider(color = Soft)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Notifications, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Nhắc tôi", fontSize = 15.sp, color = Ink)
                        Text("Trước 15 phút", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = form.remind, onCheckedChange = addViewModel::onRemind,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF34C759),
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }
            Label("Mức ưu tiên")
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Line).padding(4.dp)
            ) {
                listOf("low" to "Thấp", "medium" to "Vừa", "high" to "Cao").forEach { (key, text) ->
                    val selected = form.priority == key
                    Box(
                        modifier = Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Panel else Color.Transparent)
                            .clickable { addViewModel.onPriority(key) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text, fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) Ink else Color.Gray)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        Button(
            onClick = { save() },
            enabled = canSave,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue, contentColor = Color.White,
                disabledContainerColor = Color(0xFFE3E6F0), disabledContentColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).height(52.dp)
        ) {
            Text(if (home.isSaving) "Đang lưu..." else "Lưu công việc", fontWeight = FontWeight.Medium)
        }
    }

    if (showTimePicker) {
        val pickerState = rememberTimePickerState(
            initialHour = form.time.substringBefore(":").toIntOrNull() ?: 19,
            initialMinute = form.time.substringAfter(":").toIntOrNull() ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ", fontWeight = FontWeight.Bold) },
            text = { TimePicker(state = pickerState) },
            confirmButton = {
                TextButton(onClick = {
                    addViewModel.onTime("%02d:%02d".format(pickerState.hour, pickerState.minute))
                    showTimePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Huỷ") } }
        )
    }
}

@Composable
private fun Label(text: String) {
    Text(text, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
}

@Composable
private fun ChoiceBox(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Blue.copy(alpha = 0.10f) else Panel)
            .border(1.5.dp, if (selected) Blue else Line, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = if (selected) Blue else Ink)
    }
}

@Composable
private fun plainFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent
)