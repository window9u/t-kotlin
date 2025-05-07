package com.example.todolist.controller

import com.example.todolist.dto.GetQuizResponse
import com.example.todolist.dto.GetQuizzesResponse
import com.example.todolist.dto.PostQuizRequest
import com.example.todolist.dto.Quiz
import com.example.todolist.service.QuizService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/class/{classId}/quiz")
class QuizController(
    private val quizService: QuizService
) {
    @PostMapping
    fun postQuiz(
        @PathVariable classId: Long,
        @RequestBody quizRequest: PostQuizRequest
    ): ResponseEntity<Void>{
        val userId = SecurityContextHolder.getContext().authentication.principal as kotlin.String

        quizService.createQuiz(
            classId = classId,
            userId = userId,
            description = quizRequest.description,
            options = quizRequest.options,
            correctAnswerIndex = quizRequest.correctAnswerIndex
        )

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    fun getQuizzes(
        @PathVariable classId: Long,
        @RequestParam status: kotlin.String
    ): ResponseEntity<GetQuizzesResponse>{
        val quizzes = quizService.findQuizzes(
            classId = classId,
            status = status
        )

        val res = quizzes.map {
            quiz -> Quiz(
                quizId = quiz.id,
                description = quiz.description,
                createdAt = quiz.createdAt
            )
        }

        return ResponseEntity.ok(GetQuizzesResponse(res))
    }

    @GetMapping("/{quizId}")
    fun getQuiz(
        @PathVariable classId: Long,
        @PathVariable quizId: Long
    ): ResponseEntity<GetQuizResponse>{
        val quiz = quizService.findQuizDetail(
            classId = classId,
            quizId = quizId
        )
        val optionsMap: Map<Long, String> = quiz.options.associate { option ->
            // 각 Options 객체에 대해 Pair(id, description)를 반환
            // Kotlin에서는 Pair(a, b) 대신 a to b 구문을 사용할 수 있습니다.
            option.id to option.description
        }

        val res = GetQuizResponse(
            id = quiz.id,
            description = quiz.description,
            options = optionsMap,
            createdAt = quiz.createdAt
        )

        return ResponseEntity.ok(res)
    }

    @DeleteMapping("/{quizId}")
    fun deleteQuiz(
        @PathVariable classId: Long,
        @PathVariable quizId: Long
    ): ResponseEntity<Void>{
        val userId = SecurityContextHolder.getContext().authentication.principal as kotlin.String
        quizService.deleteQuiz(
            userId = userId,
            classId = classId,
            quizId = quizId
        )

        return ResponseEntity.status(200).build()
    }

    @PutMapping("/{quizId}")
    fun updateQuiz(
        @PathVariable classId: Long,
        @PathVariable quizId: Long,
        @RequestParam status: String
    ): ResponseEntity<Void>{
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        quizService.updateQuizStatus(
            userId = userId,
            classId = classId,
            quizId = quizId,
            quizStatus = status
        )

        return ResponseEntity.status(200).build()
    }



}