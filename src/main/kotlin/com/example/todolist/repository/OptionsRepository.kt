package com.example.todolist.repository

import com.example.todolist.domain.Options
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OptionsRepository: JpaRepository<Options,Long> {
}