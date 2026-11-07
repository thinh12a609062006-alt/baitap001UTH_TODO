package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.baitap001uth_todo.model.TaskRequest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (TaskRequest) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("study") }       // "study" hoặc "life"
    var priority by remember { mutableStateOf("medium") }      // "low" / "medium" / "high"
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var time by remember { mutableStateOf("08:00") }
    var done by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateLabel = runCatching {
        LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }.getOrDefault(date)

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Thêm việc mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text("Tên công việc") },
                    singleLine = true,
                    isError = titleError,
                    supportingText = { if (titleError) Text("Vui lòng nhập tên công việc") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Ghi chú (không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                Text("Danh mục", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = category == "study", onClick = { category = "study" },
                        label = { Text("Học tập") })
                    FilterChip(selected = category == "life", onClick = { category = "life" },
                        label = { Text("Cuộc sống") })
                }

                Spacer(Modifier.height(8.dp))
                Text("Mức độ ưu tiên", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = priority == "low", onClick = { priority = "low" },
                        label = { Text("Thấp") })
                    FilterChip(selected = priority == "medium", onClick = { priority = "medium" },
                        label = { Text("Vừa") })
                    FilterChip(selected = priority == "high", onClick = { priority = "high" },
                        label = { Text("Cao") })
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(dateLabel)
                    }
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(time)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Đã hoàn thành", modifier = Modifier.weight(1f))
                    Switch(checked = done, onCheckedChange = { done = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    if (title.isBlank()) {
                        titleError = true          // thiếu tên -> báo lỗi, không gửi API
                    } else {
                        onSave(
                            TaskRequest(
                                title = title.trim(), note = note.trim(),
                                category = category, date = date, time = time,
                                priority = priority, remind = false, done = done
                            )
                        )
                    }
                }
            ) { Text(if (isSaving) "Đang lưu..." else "Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Huỷ") } }
    )

    // ===== Chọn NGÀY =====
    if (showDatePicker) {
        val initialMillis = runCatching {
            LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }.getOrNull()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Huỷ") } }
        ) { DatePicker(state = pickerState) }
    }

    // ===== Chọn GIỜ =====
    if (showTimePicker) {
        val pickerState = rememberTimePickerState(
            initialHour = time.substringBefore(":").toIntOrNull() ?: 8,
            initialMinute = time.substringAfter(":").toIntOrNull() ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ", fontWeight = FontWeight.Bold) },
            text = { TimePicker(state = pickerState) },
            confirmButton = {
                TextButton(onClick = {
                    time = "%02d:%02d".format(pickerState.hour, pickerState.minute)
                    showTimePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Huỷ") } }
        )
    }
}