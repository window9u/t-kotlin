package com.example.todolist.repository

import com.example.todolist.domain.ClassRoom
import com.example.todolist.domain.User
import com.example.todolist.domain.UserClassRoom
import com.example.todolist.domain.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserClassRoomRepository : JpaRepository<UserClassRoom,Long>{


    @Query("""
        SELECT ucr FROM UserClassRoom ucr
        WHERE ucr.user = :user
        AND ucr.classRoom = :classRoom
        AND ucr.role = com.example.todolist.domain.UserRole.STUDENT
    """)
    fun findStudentClassRoomByUserAndClassName(
        @Param("user") user: User,
        @Param("classRoom") classRoom: ClassRoom
    ):  Optional<UserClassRoom>

    // 선생님 입장 시 사용: 특정 유저(선생님)가 특정 클래스 이름으로 이미 TEACHER 역할을 가지고 있는지 확인
    @Query("""
        SELECT ucr FROM UserClassRoom ucr
        JOIN ucr.classRoom cr
        WHERE ucr.user = :user
        AND cr.name = :className
        AND ucr.role = com.example.todolist.domain.UserRole.TEACHER
    """)
    fun findTeacherClassRoomByUserAndClassName(
        @Param("user") user: User,
        @Param("className") className: String
    ): Optional<UserClassRoom> // 해당 관계가 있다면 Optional로 반환
}