package com.example.todolist.controller

import com.example.todolist.dto.SignupRequest
import com.example.todolist.dto.SignupResponse
import com.example.todolist.dto.LoginRequest
import com.example.todolist.dto.LoginResponse
import com.example.todolist.service.AuthService
import com.example.todolist.utils.JwtUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// 필요한 의존성 주입 (예: JWT 관련 서비스, 인증 서비스)

@RestController
@RequestMapping("/auth")
class AuthController (
    private val authService: AuthService,
    private val jwtService: JwtUtils,
) {
    @PostMapping("/login")
    fun loginTeacher(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val user = authService.loginUser(
            userId = loginRequest.id,
            password = loginRequest.password
        )

        // 2. 인증 성공 시 JWT 생성 (jwtService를 사용하여 JWT 생성)
         val token = jwtService.generateToken(user.id)

        // 3. JWT를 응답으로 반환
        val loginResponse = LoginResponse(
            userName = user.name,
            jwtToken = token
        )

        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("/signup")
    fun loginStudent(@RequestBody signupRequest: SignupRequest): ResponseEntity<SignupResponse> {
        val user = authService.signupUser(
            userId = signupRequest.userId,
            userName = signupRequest.userName,
            password = signupRequest.password
        )

        val token = jwtService.generateToken(user.id)

        return ResponseEntity.ok(SignupResponse(
            userName = signupRequest.userName,
            token = token,
        ))
    }
}
