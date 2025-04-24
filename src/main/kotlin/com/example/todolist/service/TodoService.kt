package com.example.todolist.service

import com.example.todolist.domain.Todo
import com.example.todolist.repository.TodoRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TodoService(private val todoRepository: TodoRepository) {

    fun getAllTodos(): List<Todo> = todoRepository.findAll()

    fun getTodoById(id: Long): Todo = todoRepository.findById(id)
        .orElseThrow { NoSuchElementException("Todo with id $id not found") }

    fun createTodo(todo: Todo): Todo = todoRepository.save(todo)

    fun updateTodo(id: Long, updatedTodo: Todo): Todo {
        val existingTodo = getTodoById(id)
        val newTodo = existingTodo.copy(
            title = updatedTodo.title,
            description = updatedTodo.description,
            completed = updatedTodo.completed,
            updatedAt = LocalDateTime.now()
        )
        return todoRepository.save(newTodo)
    }

    fun deleteTodo(id: Long) {
        todoRepository.deleteById(id)
    }
}