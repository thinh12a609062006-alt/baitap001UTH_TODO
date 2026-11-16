package com.example.baitap001uth_todo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.viewmodel.HomeViewModel
import java.text.Normalizer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.saveable.rememberSaveable

private val Blue = Color(0xFF2F5BEA)
private val Orange = Color(0xFFF59E0B)
private val Red = Color(0xFFE5484D)
private val Ink = Color(0xFF1B1F3B)
private val Soft = Color(0xFFF3F5FC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: HomeViewModel,
    onHomeClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onAddTaskClick: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("all") }
    var showAdd by remember { mutableStateOf(false) }

    val visible = state.tasks
        .filter { filter == "all" || it.category == filter }
        .filter { query.isBlank() || it.title.plain().contains(query.plain()) }
    val pending = visible.filter { !it.done }
    val finished = visible.filter { it.done }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            AppBottomBar(
                selected = BottomTab.LIST,
                onHomeClick = onHomeClick,
                onListClick = {},
                onAddClick = onAddTaskClick
                ,onNotificationsClick = onNotificationsClick,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text("Danh sách", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = Ink, modifier = Modifier.weight(1f))
                Text("${state.tasks.count { !it.done }} việc chưa xong",
                    fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Tìm công việc", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Soft,
                    focusedContainerColor = Soft,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Blue
                ),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterPill("Tất cả", filter == "all") { filter = "all" }
                FilterPill("Học tập", filter == "study") { filter = "study" }
                FilterPill("Cuộc sống", filter == "life") { filter = "life" }
            }
            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue)
                }

                state.error != null -> ListErrorBox(
                    message = state.error!!,
                    onRetry = { viewModel.loadTasks() }
                )

                visible.isEmpty() -> Text(
                    if (state.tasks.isEmpty()) "Chưa có công việc nào. Bấm nút + để thêm việc đầu tiên!"
                    else "Không tìm thấy công việc phù hợp",
                    color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(32.dp)
                )

                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (pending.isNotEmpty()) {
                        item { SectionLabel("CẦN LÀM") }
                        items(pending, key = { it.id }) { task ->
                            ListTaskItem(task, onClick = { onTaskClick(task.id) }) {
                                viewModel.toggleDone(task) { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    if (finished.isNotEmpty()) {
                        item { SectionLabel("ĐÃ XONG") }
                        items(finished, key = { it.id }) { task ->
                            ListTaskItem(task, onClick = { onTaskClick(task.id) }) {
                                viewModel.toggleDone(task) { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showAdd) {
        AddTaskDialog(
            isSaving = state.isSaving,
            onDismiss = { showAdd = false },
            onSave = { request ->
                viewModel.addTask(request) { result ->
                    result
                        .onSuccess {
                            Toast.makeText(context, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show()
                            showAdd = false
                        }
                        .onFailure {
                            Toast.makeText(context, "Thêm thất bại: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
        )
    }
}

@Composable
private fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = if (selected) Color.White else Ink,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Ink else Soft)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun ListTaskItem(task: Task, onClick: () -> Unit, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Soft)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (task.done) Blue else Color.White)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (task.done) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
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
                Spacer(Modifier.width(8.dp))
                Text(whenLabel(task.date, task.time), fontSize = 11.sp, color = Color.Gray)
            }
        }

        if (task.priority == "high" && !task.done) {
            Text(
                "Gấp", fontSize = 11.sp, color = Red, fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Red.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun ListErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFDECEC))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color(0xFFB42318), fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
            Text("Thử lại")
        }
    }
}


private fun whenLabel(date: String, time: String): String {
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


private fun String.plain(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .replace('đ', 'd').replace('Đ', 'D')
        .lowercase()