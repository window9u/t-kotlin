package com.example.todolist.domain

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import kotlin.String

@Entity
@EntityListeners(AuditingEntityListener::class) // 추가 또는 수정
@Table(name = "options")
data class Options(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L,

    @Column(name = "description")
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id", nullable = false)
    val quiz: Quiz,
)
