package com.example.todolist.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import kotlin.String

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_name", columnList = "name")
    ]
)
data class User(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "name", unique = true)
    val name: String,

    @Column(name = "password_hash")
    val passwordHash: String
)