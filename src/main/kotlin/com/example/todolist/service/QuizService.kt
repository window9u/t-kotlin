package com.example.todolist.service

import com.example.todolist.domain.Quiz
import com.example.todolist.dto.GetQuizResultResponse

interface QuizService {
    fun createQuiz(classId: Long, userId: kotlin.String, description: kotlin.String, options: List<kotlin.String>, correctAnswerIndex: Int)
    fun findQuizzes(classId: Long,userId: String, status: kotlin.String): List<Quiz>
    fun deleteQuiz(userId: kotlin.String, classId: Long, quizId: Long)
    fun updateQuizStatus(userId: kotlin.String, classId: Long, quizId: Long, quizStatus: String)

    fun findQuizDetail(classId: Long, quizId: Long): Quiz

    fun submitQuiz(classId: Long, userId: String, quizId: Long, optionId: Long)

    fun findQuizResult(quizId: Long): GetQuizResultResponse
}