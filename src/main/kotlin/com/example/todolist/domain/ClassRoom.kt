package com.example.todolist.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import kotlin.String

@Entity
@Table(
    name = "class_rooms",
    indexes = [Index(name = "idx_class_rooms_user_name", columnList = "name")]
)
data class ClassRoom(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0, // Long 타입으로 변경, 초기값 설정

    @Column(name = "name")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val name: String,
)
