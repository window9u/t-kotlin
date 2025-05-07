package com.example.todolist.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class) // @CreatedDate 사용을 위해 추가
@Table(name = "quiz_logs")
data class QuizLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L, // 자동 생성 ID는 var로 선언하고 기본값 0L

    // ManyToOne 관계: 하나의 QuizLog는 하나의 Quiz에 속한다.
    // fetch = FetchType.LAZY: QuizLog를 조회할 때 Quiz 엔티티는 프록시로 가져온다. (성능상 이점)
    // JoinColumn: quiz_logs 테이블에 quiz_id 외래 키 컬럼을 만든다.
    // nullable = false: 모든 QuizLog는 반드시 연결된 Quiz가 있어야 한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id", nullable = false)
    val quiz: Quiz, // val로 두는 이유는 생성 시 결정되고 이후 변경되지 않기 때문

    // ManyToOne 관계: 하나의 QuizLog는 하나의 Options에 속한다 (사용자가 선택한 옵션).
    // nullable = false: (만약 사용자가 옵션을 필수로 선택해야 한다면) 모든 QuizLog는 반드시 선택된 Option이 있어야 한다.
    //                  만약 옵션 선택 없이 로그를 남길 수도 있다면 nullable = true로 설정한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", referencedColumnName = "id", nullable = false) // assuming option selection is required
    val option: Options, // val로 두는 이유는 생성 시 결정되고 이후 변경되지 않기 때문

    // ManyToOne 관계: 하나의 QuizLog는 한 명의 User에 속한다.
    // User 엔티티의 ID 필드가 "id"이고 타입이 String이라고 가정합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) // assuming user must be linked
    val user: User, // User 엔티티가 정의되어 있어야 함. val로 두는 이유는 생성 시 결정되고 이후 변경되지 않기 때문

    @CreatedDate // 추가
    @Column(name = "created_at", updatable = false) // 생성 시간은 한 번만 설정, 수정 불가능
    val createdAt: LocalDateTime? = null
)
