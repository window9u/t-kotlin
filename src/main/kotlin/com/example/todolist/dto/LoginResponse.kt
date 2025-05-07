package com.example.todolist.dto

data class LoginResponse( // data class로 변경
    val userName: String,
    val jwtToken: String
)
