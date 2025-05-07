package com.example.todolist.repository

import com.example.todolist.domain.ClassRoom
import com.example.todolist.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository: JpaRepository<User, String> {

    @Query("""
        SELECT u FROM User u
        JOIN UserClassRoom ucr on ucr.user = u
        WHERE ucr.classRoom = :classRoom
        AND ucr.role = com.example.todolist.domain.UserRole.STUDENT
    """)
    fun findStudentsByClassRoom(@Param("classRoom") classRoom: ClassRoom): List<User>
}
