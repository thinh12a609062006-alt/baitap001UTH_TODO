package com.example.baitap001uth_todo.model

data class Subtask(
    val title: String = "",
    val done: Boolean = false
)


data class Task(
    val id: String = "",
    val title: String = "",
    val note: String = "",
    val category: String = "study",
    val date: String = "",
    val time: String = "",
    val priority: String = "medium",
    val remind: Boolean = false,
    val done: Boolean = false,
    val subtasks: List<Subtask> = emptyList()
) {
    val categoryLabel: String
        get() = if (category == "life") "Cuộc sống" else "Học tập"
}


data class TaskRequest(
    val title: String,
    val note: String = "",
    val category: String,
    val date: String,
    val time: String,
    val priority: String = "medium",
    val remind: Boolean = false,
    val done: Boolean = false,
    val subtasks: List<Subtask> = emptyList()
)

fun Task.toRequest() = TaskRequest(
    title = title, note = note, category = category, date = date,
    time = time, priority = priority, remind = remind, done = done,
    subtasks = subtasks
)