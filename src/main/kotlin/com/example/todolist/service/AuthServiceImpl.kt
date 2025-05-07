package com.example.todolist.service

import com.example.todolist.domain.User
import com.example.todolist.repository.UsersRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl (
    private val userRepository: UsersRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : AuthService {
    @Transactional(readOnly = true)
    override fun loginUser(userId: String, password: String): User {
        // 해당 class가 존재하는지 확인
        val user = userRepository.findById(userId).orElse(null)
            ?: throw IllegalArgumentException("User not found")

        // 있으면 password로 맞는지 확인
        if (!bCryptPasswordEncoder.matches(password, user.passwordHash)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        return user
    }

    @Transactional
    override fun signupUser(userId: String, userName: String, password: String): User {
        if (userRepository.existsById(userId)){
            throw IllegalArgumentException("이미 사용 중인 ID 입니다.")
        }

        val user = User(
            name = userName,
            id = userId,
            passwordHash = bCryptPasswordEncoder.encode(password)
        )

        try {
            userRepository.save(user)
            return user
        } catch (e: DataIntegrityViolationException) { // DataIntegrityViolationException을 잡습니다.
            // 예외 처리 로직: 예를 들어, "이미 사용 중인 이름입니다."와 같은 메시지를 던질 수 있습니다.
            throw IllegalArgumentException("이미 사용 중인 이름 입니다.")
        }
    }
}