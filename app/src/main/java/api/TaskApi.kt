package com.example.baitap001uth_todo.api

import com.example.baitap001uth_todo.model.Task
import com.example.baitap001uth_todo.model.TaskRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface TaskApi {

    @GET("tasks")
    suspend fun getTasks(): List<Task>

    @POST("tasks")
    suspend fun createTask(@Body task: TaskRequest): Task

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: TaskRequest): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String)
}