package com.example.todolist.config

import com.example.todolist.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig (
    private val jwtAuthenticationFilter: JwtAuthenticationFilter // JWT 인증 필터
){

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // CSRF 보호 비활성화 (개발/테스트 용도)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션 사용 안 함
            .cors { it.configurationSource(corsConfigurationSource()) } // CORS 설정 추가
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/**").permitAll() // "/auth/**" 경로는 인증 없이 접근 가능
                    .anyRequest().authenticated() // 나머지 경로는 인증 필요
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // JWT 필터 추가

        http.formLogin { it.disable() }  // <-- 이 줄을 추가하여 폼 기반 로그인을 비활성화합니다.
        http.httpBasic { it.disable() } // <-- 이 줄을 추가하여 HTTP Basic 인증을 비활성화합니다.


        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("https://window9u.me", "http://localhost:5173") // 허용할 Origin
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
        configuration.allowedHeaders = listOf("Content-Type", "Authorization") // 허용할 헤더
        configuration.allowCredentials = true // Credentials 허용
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // 모든 경로에 대해 CORS 설정 적용
        return source
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
