package com.example.todolist.service

import com.example.todolist.domain.Quiz
import com.example.todolist.domain.QuizResult

interface QuizService {
    fun createQuiz(classId: Long, userId: kotlin.String, description: kotlin.String, options: List<kotlin.String>, correctAnswerIndex: Int)
    fun findQuizzes(classId: Long, status: kotlin.String): List<Quiz>
    fun deleteQuiz(userId: kotlin.String, classId: Long, quizId: Long)
    fun updateQuizStatus(userId: kotlin.String, classId: Long, quizId: Long, quizStatus: String)

    fun findQuizDetail(classId: Long, quizId: Long): Quiz

    fun submitQuiz(classId: Long, userId: String, quizId: Long, answerOption: Int)

    fun findQuizResult(quizId: Long): Pair<Quiz,List<Pair<Long,Long>>>
}