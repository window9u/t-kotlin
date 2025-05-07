package com.example.todolist.filter

import com.example.todolist.utils.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtils: JwtUtils,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            try {
                val uId = jwtUtils.extractUserId(token)

                // Authentication 객체 생성 (권한 정보는 필요에 따라 설정)
                val authentication = UsernamePasswordAuthenticationToken(uId, null, null) // 권한 정보는 필요에 따라 설정

                // request 정보 설정 (선택 사항) - IP 주소, 세션 ID 등을 설정할 수 있습니다.
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // SecurityContextHolder에 Authentication 객체 저장
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                println("JWT 검증 실패: ${e.message}")
            }
        }

        filterChain.doFilter(request, response)
    }
}