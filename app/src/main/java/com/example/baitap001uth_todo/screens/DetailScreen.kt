package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baitap001uth_todo.model.Subtask
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Orange = Color(0xFFF59E0B)
private val Red = Color(0xFFE5484D)

private data class SubEdit(val index: Int, val text: String)

@Composable
fun DetailScreen(
    taskId: String,
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val detail by viewModel.detail.collectAsStateWithLifecycle()
    val task = detail.task

    LaunchedEffect(taskId) { viewModel.loadDetail(taskId) }

    var showEdit by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var subEdit by remember { mutableStateOf<SubEdit?>(null) }

    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    fun save(updated: Task, okMessage: String? = null) {
        viewModel.saveDetail(updated) { result ->
            result
                .onSuccess { okMessage?.let { toast(it) } }
                .onFailure { toast("Cập nhật thất bại: ${it.message}") }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Panel)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Blue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Quay lại", color = Blue, fontSize = 15.sp)
            }
            if (task != null) {
                Text("Sửa", color = Blue, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { showEdit = true })
            }
        }

        when {
            task == null && detail.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue)
            }

            task == null -> Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
                    .clip(RoundedCornerShape(14.dp)).background(Color(0xFFFDECEC)).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(detail.error ?: "Không tải được công việc", color = Color(0xFFB42318),
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Button(onClick = { viewModel.loadDetail(taskId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue)) { Text("Thử lại") }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val chipColor = if (task.category == "life") Orange else Blue
                        Chip(task.categoryLabel, chipColor)
                        Chip(priorityLabel(task.priority), Color.Gray)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        task.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink,
                        textDecoration = if (task.done) TextDecoration.LineThrough else null
                    )

                    Spacer(Modifier.height(16.dp))
                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Soft)) {
                        InfoRow(Icons.Default.DateRange, "Hạn chót", dueLabel(task.date, task.time))
                        HorizontalDivider(color = Bg)
                        InfoRow(Icons.Default.Notifications, "Nhắc nhở",
                            if (task.remind) "Trước 15 phút" else "Không nhắc")
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Ghi chú", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        task.note.ifBlank { "Chưa có ghi chú" },
                        color = if (task.note.isBlank()) Color.Gray else Ink,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(Soft).padding(14.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    val doneCount = task.subtasks.count { it.done }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Việc nhỏ", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            color = Color.Gray, modifier = Modifier.weight(1f))
                        Text("$doneCount/${task.subtasks.size}", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (task.subtasks.isEmpty()) 0f else doneCount.toFloat() / task.subtasks.size },
                        color = Blue, trackColor = Soft,
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
                    )
                    Spacer(Modifier.height(8.dp))

                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Soft)) {
                        task.subtasks.forEachIndexed { index, sub ->
                            SubtaskRow(
                                sub = sub,
                                onToggle = {
                                    save(task.copy(subtasks = task.subtasks.mapIndexed { i, s ->
                                        if (i == index) s.copy(done = !s.done) else s
                                    }))
                                },
                                onEdit = { subEdit = SubEdit(index, sub.title) },
                                onDelete = {
                                    save(task.copy(subtasks = task.subtasks.filterIndexed { i, _ -> i != index }),
                                        "Đã xoá việc nhỏ")
                                }
                            )
                            HorizontalDivider(color = Bg)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { subEdit = SubEdit(-1, "") }.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("+  Thêm việc nhỏ", color = Blue, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            save(task.copy(done = !task.done),
                                if (task.done) "Đã bỏ đánh dấu hoàn thành" else "Đã hoàn thành công việc")
                        },
                        enabled = !detail.isWorking,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(if (task.done) "Bỏ đánh dấu hoàn thành" else "Đánh dấu hoàn thành",
                            fontWeight = FontWeight.Medium)
                    }
                    TextButton(onClick = { showDelete = true }, enabled = !detail.isWorking) {
                        Text("Xóa công việc", color = Red)
                    }
                }
            }
        }
    }
    if (showEdit && task != null) {
        AddTaskDialog(
            isSaving = detail.isWorking,
            initial = task,
            onDismiss = { showEdit = false },
            onSave = { req ->
                viewModel.saveDetail(
                    task.copy(
                        title = req.title, note = req.note, category = req.category,
                        date = req.date, time = req.time, priority = req.priority, done = req.done
                    )
                ) { result ->
                    result
                        .onSuccess { toast("Đã cập nhật công việc"); showEdit = false }
                        .onFailure { toast("Cập nhật thất bại: ${it.message}") }
                }
            }
        )
    }
    if (showDelete && task != null) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Xóa công việc", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc muốn xóa \"${task.title}\" không? Việc này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    viewModel.deleteTask(task.id) { result ->
                        result
                            .onSuccess { toast("Đã xóa công việc"); onBack() }
                            .onFailure { toast("Xóa thất bại: ${it.message}") }
                    }
                }) { Text("Xóa", color = Red) }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Huỷ") } }
        )
    }

    subEdit?.let { edit ->
        var text by remember(edit) { mutableStateOf(edit.text) }
        AlertDialog(
            onDismissRequest = { subEdit = null },
            title = {
                Text(if (edit.index < 0) "Thêm việc nhỏ" else "Sửa việc nhỏ", fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    label = { Text("Tên việc nhỏ") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    enabled = text.isNotBlank() && task != null,
                    onClick = {
                        if (task != null) {
                            val newList = if (edit.index < 0) {
                                task.subtasks + Subtask(title = text.trim())
                            } else {
                                task.subtasks.mapIndexed { i, s ->
                                    if (i == edit.index) s.copy(title = text.trim()) else s
                                }
                            }
                            save(task.copy(subtasks = newList))
                        }
                        subEdit = null
                    }
                ) { Text("Lưu") }
            },
            dismissButton = { TextButton(onClick = { subEdit = null }) { Text("Huỷ") } }
        )
    }
}

@Composable
private fun Chip(text: String, color: Color) {
    Text(
        text, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SubtaskRow(sub: Subtask, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (sub.done) Blue else Panel)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (sub.done) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            sub.title, fontSize = 14.sp, modifier = Modifier.weight(1f),
            color = if (sub.done) Color.Gray else Ink,
            textDecoration = if (sub.done) TextDecoration.LineThrough else null
        )
        Icon(Icons.Default.Edit, "Sửa", tint = Color.Gray,
            modifier = Modifier.size(20.dp).clickable { onEdit() })
        Spacer(Modifier.width(14.dp))
        Icon(Icons.Default.Delete, "Xóa", tint = Red,
            modifier = Modifier.size(20.dp).clickable { onDelete() })
    }
}

private fun priorityLabel(priority: String) = when (priority) {
    "high" -> "Ưu tiên cao"
    "low" -> "Ưu tiên thấp"
    else -> "Ưu tiên vừa"
}


private fun dueLabel(date: String, time: String): String {
    val day = runCatching { LocalDate.parse(date) }.getOrNull()
    val today = LocalDate.now()
    val dayText = when (day) {
        null -> date
        today -> "Hôm nay"
        today.plusDays(1) -> "Ngày mai"
        else -> day.format(DateTimeFormatter.ofPattern("dd/MM"))
    }
    return listOf(dayText, time).filter { it.isNotBlank() }.joinToString(", ")
}