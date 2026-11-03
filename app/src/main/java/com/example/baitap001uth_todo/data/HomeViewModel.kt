package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitap001uth_todo.model.Todo
import com.example.baitap001uth_todo.repository.TodoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TodoRepository = TodoRepository()
) : ViewModel() {

    // Danh sách realtime từ Firestore (của user đang đăng nhập)
    val todos: StateFlow<List<Todo>> = repository.getTodosFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Thông báo lỗi để UI hiện Toast
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    fun clearError() { _error.value = null }

    private fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.localizedMessage ?: "không xác định"}"
            }
        }
    }

    fun toggle(todo: Todo) = launchSafe {
        repository.setCompleted(todo.id, !todo.isCompleted)
    }

    fun addTodo(title: String, category: String, time: String, date: String) = launchSafe {
        repository.addTodo(Todo(title = title, category = category, time = time, date = date))
    }

    fun updateTodo(todo: Todo, title: String, category: String, time: String) = launchSafe {
        repository.updateTodo(todo.id, title, category, time)
    }

    fun deleteTodo(todo: Todo) = launchSafe {
        repository.deleteTodo(todo.id)
    }
}