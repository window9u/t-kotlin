package com.example.todolist.repository

import com.example.todolist.domain.Options
import com.example.todolist.domain.Quiz
import com.example.todolist.domain.QuizLog
import com.example.todolist.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface QuizLogRepository: JpaRepository<QuizLog,Long> {
    fun findByUserAndQuiz(user: User, quiz: Quiz): QuizLog?

    @Query("""
        SELECT ql.option.id, COUNT(ql)
        FROM QuizLog ql
        WHERE ql.quiz = :quiz
        GROUP BY ql.option.id
    """)
    fun countOptionSelectionsByQuizIdOnly(@Param("quiz") quiz: Quiz): List<Pair<Long, Long>>
}