package com.example.todolist.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "user_classroom", // 중간 테이블 이름
    uniqueConstraints = [ // user_id와 classroom_id 쌍은 유일해야 함 (같은 유저가 같은 클래스에 두 번 가입할 수 없도록)
        UniqueConstraint(columnNames = ["user_id", "classroom_id"])
    ]
)
data class UserClassRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0, // 멤버십 자체의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // User와 연결
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false) // ClassRoom과 연결
    val classRoom: ClassRoom,

    @Enumerated(EnumType.STRING) // Enum 값을 문자열("TEACHER", "STUDENT")로 DB에 저장
    @Column(name = "role", nullable = false) // 이 관계에 대한 속성
    val role: UserRole, // 타입 변경

    @Column(name = "accessed_at")
    var accessedAt: LocalDate
)