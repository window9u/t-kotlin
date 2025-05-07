package com.example.todolist.service

import com.example.todolist.domain.*
import com.example.todolist.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class QuizServiceImpl(
    private val usersRepository: UsersRepository,
    private val classRoomRepository: ClassRoomRepository,
    private val userClassRoomRepository: UserClassRoomRepository,
    private val quizRepository: QuizRepository,
    private val quizLogRepository: QuizLogRepository
): QuizService {

    @Transactional
    override fun createQuiz(
        classId: Long,
        userId: kotlin.String,
        description: kotlin.String,
        options: List<kotlin.String>,
        correctAnswerIndex: Int // 어떤 옵션이 정답인지 정보 추가
    ) {
        // 1. 기본 엔티티 유효성 검사 (기존 코드와 동일)
        val user = usersRepository.findById(userId)
            .orElseThrow { throw IllegalArgumentException("there is no user") }
        val classRoom = classRoomRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("there is no class") }

        userClassRoomRepository.findTeacherClassRoomByUserAndClassName(
            user = user,
            className = classRoom.name
        ).orElseThrow {
            throw IllegalArgumentException("invalid access")
        }

        // 옵션 개수 및 정답 인덱스 유효성 검사
        if (options.isEmpty()) {
            throw IllegalArgumentException("Quiz must have at least one option")
        }
        if (correctAnswerIndex < 0 || correctAnswerIndex >= options.size) {
            throw IllegalArgumentException("Invalid correct answer index")
        }

        // 2. Quiz 엔티티 인스턴스 생성 (ID는 아직 없음)
        val quiz = Quiz(
            status = QuizStatus.Available, // 초기 상태 설정
            classRoom = classRoom,
            description = description
            // options 리스트와 answer는 생성 시 기본값(빈 리스트, null)으로 시작
            // ID와 createdAt은 DB에서 자동 생성/설정
        )

// 3. Option 엔티티 인스턴스들을 생성하고 Quiz 엔티티에 연결
        val createdOptionsList = options.map { optionDescription ->
            val option = Options(description = optionDescription, quiz = quiz)
            // option.quiz = quiz 는 필수 (JPA가 DB 관계를 위해 필요)
            option
        }
        quiz.options = createdOptionsList.toMutableList() // Quiz 객체의 options 리스트에 할당

        // 4. 정답 Option 설정
        val answerOption = createdOptionsList[correctAnswerIndex]
        quiz.answer = answerOption // <-- 정답 Option 엔티티 인스턴스를 연결 (아직 ID는 없지만 JPA가 처리)

        // 5. Quiz 엔티티 저장
        // Quiz 엔티티에 cascade = [CascadeType.PERSIST]가 설정되어 있으므로,
        // Quiz 인스턴스의 options 리스트와 answer 필드에 연결된
        // 아직 저장되지 않은 Option 엔티티들이 Quiz 저장 시 함께 저장됩니다.
        // JPA는 이 과정에서 Option들을 먼저 저장하고 ID를 할당한 후,
        // Quiz를 저장할 때 Option들의 ID를 참조하여 foreign key를 설정합니다.
        quizRepository.save(quiz)
    }

    override fun findQuizzes(classId: Long, status: String): List<Quiz> {
        val classRoom = classRoomRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("class not found") }
        // 2. status String을 QuizStatus enum으로 변환 (유효성 검사 포함)
        val quizStatus: QuizStatus
        try {
            // String.valueOf(status)는 null 체크를 더 잘하지만, 여기서는 non-null String이므로 직접 enum 값을 찾는 것이 일반적.
            // QuizStatus enum에 해당하는 문자열이 아니면 IllegalArgumentException 발생
            quizStatus = QuizStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 status 문자열이 들어왔을 경우 예외 처리
            val validStatuses = QuizStatus.values().joinToString(", ") { it.name }
            throw IllegalArgumentException("Invalid quiz status '$status'. Valid statuses are: $validStatuses")
        }

        return quizRepository.findAllByClassRoomAndStatus(classRoom, quizStatus )
    }

    @Transactional
    override fun deleteQuiz(userId: String, classId: Long, quizId: Long) {
        val user = usersRepository.findById(userId)
            .orElseThrow { throw IllegalArgumentException("there is no user") }
        val classRoom = classRoomRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("there is no class") }

        userClassRoomRepository.findTeacherClassRoomByUserAndClassName(
            user = user,
            className = classRoom.name
        ).orElseThrow {
            throw IllegalArgumentException("invalid access")
        }

        val quiz = quizRepository.findById(quizId)
            .orElseThrow { throw IllegalArgumentException("quiz not found")}

        if (quiz.classRoom != classRoom){
            throw IllegalArgumentException("invalid access")
        }
        quizRepository.delete(quiz)
    }

    @Transactional
    override fun updateQuizStatus(userId: String, classId: Long, quizId: Long, status: String) {
        val user = usersRepository.findById(userId)
            .orElseThrow { throw IllegalArgumentException("there is no user") }
        val classRoom = classRoomRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("there is no class") }

        userClassRoomRepository.findTeacherClassRoomByUserAndClassName(
            user = user,
            className = classRoom.name
        ).orElseThrow {
            throw IllegalArgumentException("invalid access")
        }

        val quiz = quizRepository.findById(quizId)
            .orElseThrow { throw IllegalArgumentException("quiz not found")}

        if (quiz.classRoom != classRoom){
            throw IllegalArgumentException("invalid access")
        }

        val quizStatus: QuizStatus
        try {
            quizStatus = QuizStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 status 문자열이 들어왔을 경우 예외 처리
            val validStatuses = QuizStatus.values().joinToString(", ") { it.name }
            throw IllegalArgumentException("Invalid quiz status '$status'. Valid statuses are: $validStatuses")
        }

        quiz.status = quizStatus
    }

    override fun findQuizDetail(classId: Long, quizId: Long): Quiz {
        return quizRepository.findQuizWithOption(quizId)
            .orElseThrow { throw IllegalArgumentException("there is no quiz") }
    }

    override fun findQuizResult(quizId: Long): Pair<Quiz,List<Pair<Long,Long>>> {
        val quiz = quizRepository.findQuizWithOptionAndAnswer(quizId)
            .orElseThrow { throw IllegalArgumentException("quiz not found") }

        val results = quizLogRepository.countOptionSelectionsByQuizIdOnly(quiz)

        return Pair(quiz,results)
    }

    override fun submitQuiz(classId: Long, userId: String, quizId: Long, answerOption: Int) {
        val user = usersRepository.findById(userId)
            .orElseThrow { throw IllegalArgumentException("user not found")}
        val quiz = quizRepository.findQuizWithOption(quizId)
            .orElseThrow { throw IllegalArgumentException("quiz not found")}

        if (quizLogRepository.findByUserAndQuiz(user, quiz) != null) {
            throw IllegalArgumentException("user already solved problem")
        }

        val quizLog = QuizLog(
            quiz = quiz,
            user = user,
            option = quiz.options[answerOption]
        )
        quizLogRepository.save(quizLog)
    }
}