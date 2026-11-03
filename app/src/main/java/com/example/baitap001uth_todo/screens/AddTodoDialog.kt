package com.example.baitap001uth_todo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.baitap001uth_todo.model.Todo

/**
 * initial = null  -> chế độ THÊM
 * initial != null -> chế độ SỬA (có thêm nút Xoá)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    initial: Todo? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, time: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember(initial) { mutableStateOf(initial?.title ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: "Học tập") }
    var time by remember(initial) { mutableStateOf(initial?.time ?: "") }

    // Giờ để trống hoặc đúng dạng HH:mm (vd 19:00)
    val timeValid = time.isEmpty() || Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(time)

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
                OutlinedTextField(
                    value = time, onValueChange = { time = it.take(5) },
                    label = { Text("Giờ (vd 19:00, có thể bỏ trống)") }, singleLine = true,
                    isError = !timeValid,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Học tập", "Cuộc sống").forEach { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row {
                if (initial != null && onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Xoá", color = Color(0xFFE5484D)) }
                }
                TextButton(
                    onClick = { onSave(title.trim(), category, time) },
                    enabled = title.isNotBlank() && timeValid
                ) { Text(if (initial == null) "Thêm" else "Lưu") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}

