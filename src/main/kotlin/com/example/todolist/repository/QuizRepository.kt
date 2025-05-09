package com.example.todolist.repository

import com.example.todolist.domain.ClassRoom
import com.example.todolist.domain.Quiz
import com.example.todolist.domain.QuizStatus
import com.example.todolist.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizRepository: JpaRepository<Quiz,Long> {
    @Query("""
        SELECT q FROM Quiz q
        WHERE q.classRoom = :classRoom
         AND q.status = :status
         AND q.id NOT IN(
            SELECT  ql.quiz.id FROM QuizLog ql
            WHERE ql.user = :user
         )
    """)
    fun findAllByClassRoomAndStatus(
        @Param("classRoom") classRoom: ClassRoom,
        @Param("user") user: User,
        @Param("status") status: QuizStatus
    ): List<Quiz>

    // 특정 Quiz ID로 Quiz와 그 Options를 Fetch Join으로 함께 가져오는 쿼리
    @Query("""
        SELECT q FROM Quiz q 
        JOIN FETCH q.options ops
        WHERE q.id = :quizId
    """)
    // 단일 결과를 예상하므로 Optional<Quiz> 또는 Quiz? (Nullable) 반환 타입을 사용합니다.
    // 만약 ID에 해당하는 퀴즈가 없을 수도 있다면 Optional이 더 안전합니다.
    // Optional<Quiz>는 null을 명시적으로 처리하게 해줍니다.
    fun findQuizWithOption(@Param("quizId") quizId: Long): Optional<Quiz>


    // 참고: Quiz 엔티티가 answer 필드를 가지고 있고 Options를 참조한다면, answer도 함께 Fetch Join 가능
    @Query("""
        SELECT q FROM Quiz q
        JOIN FETCH q.options ops
        LEFT JOIN FETCH q.answer ans
        WHERE q.id = :quizId
    """)
    fun findQuizWithOptionAndAnswer(@Param("quizId") quizId: Long): Optional<Quiz>
}