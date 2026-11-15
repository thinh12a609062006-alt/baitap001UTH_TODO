package com.example.baitap001uth_todo.model

enum class NotificationType { DUE_SOON, OVERDUE, GOOD_JOB, REMINDER }

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timeLabel: String,
    val unread: Boolean = true
)