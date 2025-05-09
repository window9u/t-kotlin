package com.example.todolist.dto

import java.time.LocalDateTime

data class GetQuizResultResponse(
    val id: Long,
    val description: String,
    val createdAt: LocalDateTime?,
    val options: List<OptionResultDto>, // Options 리스트 대신 OptionResultDto 리스트
    val totalAttempts: Long // 총 시도 횟수 (선택 사항)
)

data class OptionResultDto(
    val id: Long,
    val description: String,
    val isAnswer: Boolean, // 이 옵션이 정답인지 여부 (Quiz.answer와 비교)
    val selectionCount: Long, // 이 옵션이 선택된 횟수
    val selectionRate: Double // 이 옵션의 선택률 (선택 사항)
)
