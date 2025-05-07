package com.example.todolist.service

import com.example.todolist.domain.User

interface AuthService {
    fun loginUser(userId: String, password: String): User

    fun signupUser(userId: String,userName: String, password: String): User
}