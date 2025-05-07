package com.example.todolist.service

import com.example.todolist.domain.ClassRoom
import com.example.todolist.domain.User

interface ClassService {
    fun enterClassByStudent(className: String, teacherName: String, studentId: String): ClassRoom

    fun enterClassByTeacher(className: String, teacherId: String): ClassRoom

    fun findStudentsInClassRoom(classId: Long): List<User>
}
