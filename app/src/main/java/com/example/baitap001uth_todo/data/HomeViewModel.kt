package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadTasks()
    }


    fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getTasks()
                .onSuccess { list ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            tasks = list.sortedWith(compareBy({ t -> t.date }, { t -> t.time })),
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Không tải được dữ liệu")
                    }
                }
        }
    }
}