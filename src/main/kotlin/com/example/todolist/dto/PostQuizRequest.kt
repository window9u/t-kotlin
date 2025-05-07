package com.example.todolist.dto

data class PostQuizRequest(
    val description: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)