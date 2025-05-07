package com.example.todolist.controller

import com.example.todolist.domain.User
import com.example.todolist.dto.GetClassResponse
import com.example.todolist.dto.GetStudentsResponse
import com.example.todolist.dto.StudentDto
import com.example.todolist.service.ClassService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/class")
class ClassController(
    private val classService: ClassService
) {
    @GetMapping("/student")
    fun getClassIdByStudent(
        @RequestParam("className") className: String,
        @RequestParam("teacherName") teacherName: String
    ) : ResponseEntity<GetClassResponse>{
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.principal as String // JwtAuthenticationFilter에서 uId(String)을 principal로 설정했으므로 String으로 캐스팅

        val classRoom = classService.enterClassByStudent(
            className = className,
            studentId = userId,
            teacherName = teacherName
        )

        return ResponseEntity.ok(GetClassResponse(
            id = classRoom.id,
        ))
    }

    @GetMapping("/teacher")
    fun getClassIdByTeacher(
        @RequestParam("className") className: String
    ) : ResponseEntity<GetClassResponse>{
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.principal as String // JwtAuthenticationFilter에서 uId(String)을 principal로 설정했으므로 String으로 캐스팅

        val classRoom = classService.enterClassByTeacher(
            className = className,
            teacherId = userId,
        )

        return ResponseEntity.ok(GetClassResponse(
            id = classRoom.id
        ))
    }

    @GetMapping("/{classId}/students")
    fun getStudents(
         @PathVariable classId: Long
    ): ResponseEntity<GetStudentsResponse>{
        val users = classService.findStudentsInClassRoom(classId)
        println(users)
        val res = users.map {
            user: User -> StudentDto(
                studentId = user.id,
                lastActive = user.id,
                studentName = user.name
            )
        }
        return ResponseEntity.ok(GetStudentsResponse(res))
    }
}
