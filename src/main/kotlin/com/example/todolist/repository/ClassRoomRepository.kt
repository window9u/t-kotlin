package com.example.todolist.repository

import com.example.todolist.domain.ClassRoom
import com.example.todolist.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ClassRoomRepository: JpaRepository<ClassRoom,Long> {

    /**
     * 특정 선생님 이름과 클래스룸 이름으로 클래스룸을 찾습니다.
     * UserClassRoom 엔티티를 조인하여 역할이 'TEACHER'인 사용자와 연결된 클래스룸을 찾습니다.
     *
     * @param className 찾을 클래스룸 이름
     * @param teacherName 찾을 선생님 사용자 이름
     * @return 조건을 만족하는 ClassRoom (Optional로 래핑)
     */
    @Query("""
        SELECT cr FROM ClassRoom cr
        JOIN UserClassRoom ucr ON ucr.classRoom = cr
        JOIN ucr.user u
        WHERE cr.name = :className
        AND u.name = :teacherName
        AND ucr.role = com.example.todolist.domain.UserRole.TEACHER
    """) // Kotlin의 멀티라인 문자열 (Triple quotes)을 사용하면 쿼리 가독성이 좋습니다.
    fun findByClassNameAndTeacherNameAndRoleTeacher(
        @Param("className") className: String,
        @Param("teacherName") teacherName: String
    ): Optional<ClassRoom> // 반환 타입은 Optional<ClassRoom> 또는 ClassRoom? (nullable ClassRoom) 둘 다 가능하지만, Spring Data JPA에서는 Optional을 자주 사용합니다.

    // 만약 UserClassRoom의 role 필드가 Enum 타입이라면 JPQL에서 Enum 상수를 사용해야 합니다.
    // 예: AND ucr.role = com.example.todolist.domain.UserRole.TEACHER

    override fun findById(id: Long): Optional<ClassRoom>
}
