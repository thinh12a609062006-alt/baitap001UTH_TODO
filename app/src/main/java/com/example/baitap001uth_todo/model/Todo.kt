package com.example.baitap001uth_todo.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Todo(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "Cuộc sống",
    val time: String = "",
    @get:PropertyName("isCompleted") @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,
    val date: String = "",
    val createdAt: Timestamp = Timestamp.now()
)