package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.model.TaskRequest
import com.example.baitap001uth_todo.model.toRequest
import com.example.baitap001uth_todo.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

data class DetailUiState(
    val isLoading: Boolean = true,
    val isWorking: Boolean = false,
    val task: Task? = null,
    val error: String? = null
)

class HomeViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _detail = MutableStateFlow(DetailUiState())
    val detail: StateFlow<DetailUiState> = _detail.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _state.update { it.copy(isLoading = true, error = null) }
            repository.getTasks()
                .onSuccess { list ->
                    _state.update { it.copy(isLoading = false, tasks = sortTasks(list), error = null) }
                }
                .onFailure { e ->
                    _state.update {
                        if (showLoading) it.copy(isLoading = false, error = e.message ?: "Không tải được dữ liệu")
                        else it
                    }
                }
        }
    }

    fun addTask(request: TaskRequest, onResult: (Result<Task>) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val result = repository.createTask(request)
            result
                .onSuccess { created ->
                    _state.update { it.copy(isSaving = false, tasks = sortTasks(it.tasks + created)) }
                }
                .onFailure { _state.update { it.copy(isSaving = false) } }
            onResult(result)
            if (result.isSuccess) loadTasks(showLoading = false)
        }
    }

    fun toggleDone(task: Task, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            repository.updateTask(task.id, task.copy(done = !task.done).toRequest())
                .onSuccess { updated -> replaceInList(updated) }
                .onFailure { onError(it.message ?: "Không cập nhật được công việc") }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            val cached = _state.value.tasks.find { it.id == id }
            _detail.value = DetailUiState(isLoading = cached == null, task = cached)

            repository.getTask(id)
                .onSuccess { fresh ->
                    _detail.update { it.copy(isLoading = false, task = fresh, error = null) }
                    replaceInList(fresh)
                }
                .onFailure { e ->
                    _detail.update {
                        it.copy(isLoading = false, error = if (it.task == null) e.message else null)
                    }
                }
        }
    }

    fun saveDetail(updated: Task, onResult: (Result<Task>) -> Unit) {
        viewModelScope.launch {
            _detail.update { it.copy(isWorking = true) }
            val result = repository.patchTask(updated.id, updated.toRequest())
            result
                .onSuccess { saved ->
                    _detail.update { it.copy(isWorking = false, task = saved) }
                    replaceInList(saved)
                }
                .onFailure { _detail.update { it.copy(isWorking = false) } }
            onResult(result)
        }
    }

    fun deleteTask(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _detail.update { it.copy(isWorking = true) }
            val result = repository.deleteTask(id)
            result
                .onSuccess {
                    _state.update { s -> s.copy(tasks = s.tasks.filter { it.id != id }) }
                    _detail.value = DetailUiState(isLoading = true)
                }
                .onFailure { _detail.update { it.copy(isWorking = false) } }
            onResult(result)
        }
    }

    private fun replaceInList(task: Task) {
        _state.update { s ->
            s.copy(tasks = sortTasks(s.tasks.map { if (it.id == task.id) task else it }))
        }
        _detail.update { if (it.task?.id == task.id) it.copy(task = task) else it }
    }

    private fun sortTasks(list: List<Task>) =
        list.sortedWith(compareBy<Task>({ it.date }, { it.time }))
}