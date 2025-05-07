package com.example.todolist.dto

import java.time.LocalDateTime

data class GetQuizResponse(
    val id: Long,
    val description: String,
    val options: Map<Long, String>,
    val createdAt: LocalDateTime?
)
