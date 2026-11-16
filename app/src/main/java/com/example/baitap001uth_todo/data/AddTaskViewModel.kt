package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.baitap001uth_todo.model.TaskRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class DateChoice { TODAY, TOMORROW, THIS_WEEK }
data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val category: String = "study",
    val dateChoice: DateChoice = DateChoice.TODAY,
    val time: String = "19:00",
    val remind: Boolean = true,
    val priority: String = "medium"
) {
    val canSave: Boolean get() = title.isNotBlank()
    fun resolvedDate(): LocalDate {
        val today = LocalDate.now()
        return when (dateChoice) {
            DateChoice.TODAY -> today
            DateChoice.TOMORROW -> today.plusDays(1)
            DateChoice.THIS_WEEK -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        }
    }
}

class AddTaskViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddTaskUiState())
    val state: StateFlow<AddTaskUiState> = _state.asStateFlow()

    fun onTitle(v: String) = _state.update { it.copy(title = v) }
    fun onNote(v: String) = _state.update { it.copy(note = v) }
    fun onCategory(v: String) = _state.update { it.copy(category = v) }
    fun onDateChoice(v: DateChoice) = _state.update { it.copy(dateChoice = v) }
    fun onTime(v: String) = _state.update { it.copy(time = v) }
    fun onRemind(v: Boolean) = _state.update { it.copy(remind = v) }
    fun onPriority(v: String) = _state.update { it.copy(priority = v) }
    fun buildRequest(): TaskRequest? {
        val s = _state.value
        if (!s.canSave) return null
        return TaskRequest(
            title = s.title.trim(),
            note = s.note.trim(),
            category = s.category,
            date = s.resolvedDate().toString(),
            time = s.time,
            priority = s.priority,
            remind = s.remind,
            done = false
        )
    }
}