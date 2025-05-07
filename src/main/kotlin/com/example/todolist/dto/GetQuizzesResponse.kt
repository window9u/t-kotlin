package com.example.todolist.dto

import java.time.LocalDateTime

data class GetQuizzesResponse(
    val quizzes: List<Quiz>
)

data class Quiz(
    val quizId: Long,
    val description: String,
    val createdAt: LocalDateTime?
)
