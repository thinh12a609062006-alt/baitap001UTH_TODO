package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitap001uth_todo.model.AppNotification
import com.example.baitap001uth_todo.model.NotificationType
import com.example.baitap001uth_todo.model.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificationViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<AppNotification>>(emptyList())
    val items: StateFlow<List<AppNotification>> = _items.asStateFlow()

    private var timerJob: Job? = null

    fun start(tasks: List<Task>) {
        timerJob?.cancel()
        _items.value = buildNotifications(tasks, LocalDateTime.now())
        timerJob = viewModelScope.launch {
            delay(10_000)
            _items.value = emptyList()
        }
    }

    fun markAllRead() {
        _items.update { list -> list.map { it.copy(unread = false) } }
    }

    private fun buildNotifications(tasks: List<Task>, now: LocalDateTime): List<AppNotification> {
        val today = now.toLocalDate()
        val clock = now.format(DateTimeFormatter.ofPattern("HH:mm"))
        val dayFmt = DateTimeFormatter.ofPattern("dd/MM")

        val pending = tasks.filter { !it.done }.mapNotNull { t -> dueOf(t)?.let { t to it } }
        val result = mutableListOf<AppNotification>()
        pending.filter { (_, due) -> due >= now && due <= now.plusHours(24) }
            .sortedBy { it.second }
            .take(2)
            .forEach { (t, due) ->
                val extra = if (due.toLocalDate() == today) "" else " ngày ${due.format(dayFmt)}"
                result += AppNotification(
                    id = "soon_${t.id}", type = NotificationType.DUE_SOON,
                    title = "Sắp đến hạn", message = "${t.title} lúc ${t.time}$extra.",
                    timeLabel = clock
                )
            }
        pending.filter { (_, due) -> due < now }
            .sortedByDescending { it.second }
            .take(2)
            .forEach { (t, due) ->
                result += AppNotification(
                    id = "over_${t.id}", type = NotificationType.OVERDUE,
                    title = "Đã quá hạn", message = "${t.title} chưa xong.",
                    timeLabel = due.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            }
        val doneToday = tasks.count { it.done && it.date == today.toString() }
        val doneAll = tasks.count { it.done }
        if (doneAll > 0) {
            result += AppNotification(
                id = "good", type = NotificationType.GOOD_JOB,
                title = "Làm tốt lắm!",
                message = if (doneToday > 0) "Bạn đã xong $doneToday việc hôm nay."
                else "Bạn đã hoàn thành $doneAll việc.",
                timeLabel = clock, unread = false
            )
        }
        pending.filter { (_, due) -> due > now.plusHours(24) && due <= now.plusDays(7) }
            .sortedBy { it.second }
            .take(2)
            .forEach { (t, due) ->
                val label = when (due.toLocalDate()) {
                    today.plusDays(1) -> "Ngày mai"
                    else -> due.format(dayFmt)
                }
                result += AppNotification(
                    id = "remind_${t.id}", type = NotificationType.REMINDER,
                    title = "Nhắc việc", message = "${t.title} trong tuần này.",
                    timeLabel = label, unread = false
                )
            }

        return result
    }

    private fun dueOf(t: Task): LocalDateTime? = runCatching {
        val date = LocalDate.parse(t.date)
        val time = if (t.time.isBlank()) LocalTime.of(23, 59) else LocalTime.parse(t.time)
        LocalDateTime.of(date, time)
    }.getOrNull()
}