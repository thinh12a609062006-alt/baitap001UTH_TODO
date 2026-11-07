package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.model.TaskRequest
import com.example.baitap001uth_todo.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,          // đang tải danh sách
    val isSaving: Boolean = false,          // đang gửi POST
    val tasks: List<Task> = emptyList(),    // dữ liệu lấy từ API
    val error: String? = null               // lỗi khi tải danh sách
)

class HomeViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadTasks()      // mở Trang chủ là gọi GET
    }

    /** GET toàn bộ việc. showLoading = false: tải ngầm, không hiện vòng xoay. */
    fun loadTasks(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _state.update { it.copy(isLoading = true, error = null) }
            repository.getTasks()
                .onSuccess { list ->
                    _state.update { it.copy(isLoading = false, tasks = sortTasks(list), error = null) }
                }
                .onFailure { e ->
                    _state.update {
                        // Tải ngầm thất bại thì giữ nguyên danh sách đang có
                        if (showLoading) it.copy(isLoading = false, error = e.message ?: "Không tải được dữ liệu")
                        else it
                    }
                }
        }
    }

    /** POST một việc mới. onResult cho giao diện biết thành công hay thất bại. */
    fun addTask(request: TaskRequest, onResult: (Result<Task>) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val result = repository.createTask(request)

            result
                .onSuccess { created ->
                    // POST thành công: hiện việc mới ngay (máy chủ đã trả về việc kèm id)
                    _state.update { it.copy(isSaving = false, tasks = sortTasks(it.tasks + created)) }
                }
                .onFailure {
                    // POST thất bại: KHÔNG thêm gì vào danh sách
                    _state.update { it.copy(isSaving = false) }
                }

            onResult(result)

            // Bước 9: gọi lại GET để xác nhận việc đã được lưu trên máy chủ
            if (result.isSuccess) loadTasks(showLoading = false)
        }
    }

    private fun sortTasks(list: List<Task>) =
        list.sortedWith(compareBy<Task>({ it.date }, { it.time }))
}