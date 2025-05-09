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
    fun countOptionSelectionsByQuizIdOnly(@Param("quiz") quiz: Quiz):  List<Array<Any>>
    // 또는 List<Array<Long?>> 도 가능하지만, Any가 더 일반적입니다.
    // COUNT는 Long 타입으로 오지만, id는 nullable일 수 있으므로 Long? 또는 Any로 받습니다.
    // 여기서는 COUNT 결과는 Long으로 확실하기 때문에 캐스팅 시 Long으로 처리해도 안전합니다.
    fun countByQuiz(quiz: Quiz): Long

    fun existsByUserAndQuiz(user: User,quiz: Quiz): Boolean
}