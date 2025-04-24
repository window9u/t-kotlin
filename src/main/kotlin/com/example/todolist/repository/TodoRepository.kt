package com.example.todolist.repository

import com.example.todolist.domain.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    // 필요한 추가 쿼리 메서드를 정의할 수 있습니다.
    // 예: findByCompleted(completed: Boolean): List<Todo>
}