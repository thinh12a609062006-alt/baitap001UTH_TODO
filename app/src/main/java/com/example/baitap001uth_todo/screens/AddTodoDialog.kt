package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.baitap001uth_todo.model.Todo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * initial = null  -> chế độ THÊM
 * initial != null -> chế độ SỬA (có thêm nút Xoá)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    initial: Todo? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, time: String, date: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember(initial) { mutableStateOf(initial?.title ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: "Học tập") }
    var time by remember(initial) { mutableStateOf(initial?.time ?: "") }
    var date by remember(initial) {
        mutableStateOf(initial?.date?.takeIf { it.isNotBlank() } ?: LocalDate.now().toString())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Hiển thị ngày dạng 05/11/2026 cho dễ đọc (lưu lên Firestore vẫn là 2026-11-05)
    val dateLabel = runCatching {
        LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }.getOrDefault(date)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initial == null) "Thêm việc mới" else "Sửa việc", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Tên công việc") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                // Chọn danh mục
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Học tập", "Cuộc sống").forEach { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Nút chọn ngày và nút chọn giờ
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) { Text(dateLabel) }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (time.isBlank()) "Chọn giờ" else time) }
                }
            }
        },
        confirmButton = {
            Row {
                if (initial != null && onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Xoá", color = Color(0xFFE5484D)) }
                }
                TextButton(
                    onClick = { onSave(title.trim(), category, time, date) },
                    enabled = title.isNotBlank()
                ) { Text(if (initial == null) "Lưu" else "Lưu thay đổi") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )

    // ===== Bảng chọn NGÀY =====
    if (showDatePicker) {
        val initialMillis = runCatching {
            LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }.getOrNull()
        val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Huỷ") } }
        ) { DatePicker(state = state) }
    }

    // ===== Bảng chọn GIỜ =====
    if (showTimePicker) {
        val hour = time.substringBefore(":", "").toIntOrNull() ?: 8
        val minute = time.substringAfter(":", "").toIntOrNull() ?: 0
        val state = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ", fontWeight = FontWeight.Bold) },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    time = "%02d:%02d".format(state.hour, state.minute)
                    showTimePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Huỷ") } }
        )
    }
}