package com.example.baitap001uth_todo.repository

import com.example.baitap001uth_todo.api.ApiClient
import com.example.baitap001uth_todo.api.TaskApi
import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.model.TaskRequest
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class TaskRepository(private val api: TaskApi = ApiClient.taskApi) {

    suspend fun getTasks(): Result<List<Task>> = call { api.getTasks() }

    suspend fun createTask(request: TaskRequest): Result<Task> = call { api.createTask(request) }

    suspend fun updateTask(id: String, request: TaskRequest): Result<Task> =
        call { api.updateTask(id, request) }

    suspend fun deleteTask(id: String): Result<Unit> = call { api.deleteTask(id) }

    private suspend fun <T> call(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Result.failure(Exception(
            when (e.code()) {
                404 -> "Không tìm thấy dữ liệu (lỗi 404)"
                in 500..599 -> "Máy chủ đang gặp sự cố (lỗi ${e.code()})"
                else -> "API trả về lỗi ${e.code()}"
            }
        ))
    } catch (e: UnknownHostException) {
        Result.failure(Exception("Không có kết nối mạng"))
    } catch (e: SocketTimeoutException) {
        Result.failure(Exception("Kết nối quá chậm, vui lòng thử lại"))
    } catch (e: Exception) {
        Result.failure(Exception("Có lỗi xảy ra: ${e.localizedMessage ?: "không xác định"}"))
    }
}