package com.example.todolist.dto

data class GetStudentsResponse (
    val students: List<StudentDto>
)

data class StudentDto (
    val studentName: String,
    val studentId: String,
    val lastActive: String
)
