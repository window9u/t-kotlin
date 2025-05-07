package com.example.todolist.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class) // 추가 또는 수정
@Table(name = "quizzes")
data class Quiz (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: QuizStatus,

    @Column(name = "description")
    val description: kotlin.String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", referencedColumnName = "id", nullable = false)
    val classRoom: ClassRoom,

    // OneToMany 관계 설정:
    // mappedBy: Option 엔티티의 'quiz' 필드에 의해 매핑됨을 명시
    // cascade: Quiz 저장 시 Options도 함께 저장/수정/삭제되도록 설정 (PERSIST, MERGE, REMOVE 등)
    // orphanRemoval: Quiz에서 Option 제거 시 해당 Option 엔티티도 DB에서 삭제되도록 설정 (선택 사항)
    @OneToMany(
        mappedBy = "quiz",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = true
    )
    var options: MutableList<Options> = mutableListOf(), // 변경 가능하고 초기화 필요하므로 var & 기본값


    // Answer 필드 설정: Quiz의 특정 Option을 가리킴
    // ManyToOne: 하나의 Option이 여러 Quiz의 Answer가 될 수는 없지만, 구조적으로 ManyToOne이 더 자연스러울 수 있음.
    //            하지만 여기서는 해당 퀴즈의 옵션 중 하나만 정답이므로 OneToOne에 가까움.
    //            @ManyToOne(fetch = FetchType.LAZY) 또는 @OneToOne(fetch = FetchType.LAZY) 사용 가능
    //            @OneToOne으로 사용하되 JoinColumn에 unique=true 대신 answer_id 컬럼을 사용하고 nullable=true로 설정
    @ManyToOne(fetch = FetchType.LAZY) // 또는 @OneToOne
    @JoinColumn(name = "answer_id", referencedColumnName = "id", nullable = true) // JoinColumn 이름 변경, null 허용 (처음 생성 시 answer가 null일 수 있음)
    var answer: Options? = null, // 변경 가능하고 초기화 필요하므로 var & nullable

    @Column(name = "created_at", updatable = false) // 생성 시간은 한 번만 설정
    @CreatedDate
    val createdAt: LocalDateTime? = null // 기본값 추가
)


