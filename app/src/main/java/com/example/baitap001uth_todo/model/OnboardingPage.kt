package com.example.baitap001uth_todo.model

data class OnboardingPage(
    val id: Int,
    val title: String,
    val description: String,
    val buttonText: String
) {

    fun isLast(totalPages: Int): Boolean {
        return id == totalPages
    }
}