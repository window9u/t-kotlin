package com.example.todolist.service

import com.example.todolist.domain.*
import com.example.todolist.dto.GetQuizResultResponse
import com.example.todolist.dto.OptionResultDto
import com.example.todolist.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class QuizServiceImpl(
    private val usersRepository: UsersRepository,
    private val classRoomRepository: ClassRoomRepository,
    private val userClassRoomRepository: UserClassRoomRepository,
    private val quizRepository: QuizRepository,
    private val quizLogRepository: QuizLogRepository,
    private val optionsRepository: OptionsRepository
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

    override fun findQuizzes(classId: Long, userId: String, status: String): List<Quiz> {
        val classRoom = classRoomRepository.findById(classId)
            .orElseThrow { throw IllegalArgumentException("class not found") }
        // 2. status String을 QuizStatus enum으로 변환 (유효성 검사 포함)
        val user = usersRepository.findById(userId)
            .orElseThrow{ throw IllegalArgumentException("user not found")}
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



        return quizRepository.findAllByClassRoomAndStatus(classRoom,user, quizStatus )
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

    @Transactional(readOnly = true)
    override fun findQuizResult(quizId: Long): GetQuizResultResponse {
        val quiz = quizRepository.findQuizWithOptionAndAnswer(quizId) // answer도 함께 가져옴
            .orElseThrow { IllegalArgumentException("quiz not found") }

        // 2. 옵션별 선택 횟수 집계 결과 조회
        // Repository에서 List<Array<Any>> 형태로 결과를 받음
        val rawSelectionCounts: List<Array<Any>> =
            quizLogRepository.countOptionSelectionsByQuizIdOnly(quiz)

        // List<Array<Any>> 형태의 rawResults를 List<Pair<Long, Long>>으로 수동 변환
        val selectionCountsList: List<Pair<Long, Long>> = rawSelectionCounts.map { row ->
            // row[0]은 ql.option.id (예상 Long)
            // row[1]은 COUNT(ql) (예상 Long)
            // 안전하게 Long으로 캐스팅 (count는 non-null Long, id도 보통 non-null)
            Pair(row[0] as Long, row[1] as Long)
        }


        // Option ID와 개수를 Map<Long, Long> 형태로 변환하여 찾기 쉽게 만듭니다.
        // 이 부분은 기존 코드를 그대로 사용합니다.
        val selectionCountsMap: Map<Long, Long> = selectionCountsList.toMap()

        // 3. 총 시도 횟수 조회 (선택 사항)
        val totalAttempts = quizLogRepository.countByQuiz(quiz) // 이 메소드 필요

        // 4. 퀴즈의 options 리스트를 순회하며 OptionResultDto 리스트 생성
        val optionResultDtos = quiz.options.map { option ->
            val count = selectionCountsMap[option.id] ?: 0L // 해당 옵션의 선택 횟수 (없으면 0)
            OptionResultDto(
                id = option.id,
                description = option.description,
                isAnswer = quiz.answer?.id == option.id, // 정답 옵션 ID와 비교
                selectionCount = count,
                selectionRate = if (totalAttempts > 0) count.toDouble() / totalAttempts else 0.0 // 선택률 계산
            )
        }

        // 5. GetQuizResultResponse DTO 생성 및 반환
        return GetQuizResultResponse(
            id = quiz.id,
            description = quiz.description,
            createdAt = quiz.createdAt,
            options = optionResultDtos,
            totalAttempts = totalAttempts
        )
    }

    @Transactional // DB 쓰기 작업이므로 트랜잭션 어노테이션 필수
    override fun submitQuiz(classId: Long, userId: String, quizId: Long, optionId: Long) {
        // 1. 필요한 엔티티 조회 및 유효성 검사
        val user = usersRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User with id $userId not found") }
        val quiz = quizRepository.findById(quizId) // findQuizWithOption 대신 findById 사용 가능
            // Fetch Join이 꼭 필요하지 않으면 단순 조회
            .orElseThrow { IllegalArgumentException("Quiz with id $quizId not found") }
        // classId 유효성 검사가 필요하다면 여기서 추가 (예: quiz.classRoom.id != classId)

        // 2. 선택된 옵션 엔티티 조회 및 유효성 검사 (해당 퀴즈의 옵션인지 확인 포함)
        val selectedOption = optionsRepository.findById(optionId)
            .orElseThrow { IllegalArgumentException("Option with id $optionId not found") }

        // 선택된 옵션이 해당 퀴즈의 옵션인지 확인
        if (selectedOption.quiz.id != quiz.id) {
            throw IllegalArgumentException("Selected option $optionId does not belong to quiz ${quizId}")
        }


        // 3. 퀴즈 상태 확인
        if (quiz.status != QuizStatus.Active) {
            throw IllegalArgumentException("Quiz with id ${quizId} is not active. Current status: ${quiz.status}")
        }

        // 4. 사용자가 이미 이 퀴즈를 풀었는지 확인
        // QuizLogRepository에 existsByUserAndQuiz 메소드가 정의되어 있어야 합니다.
        // fun existsByUserAndQuiz(user: User, quiz: Quiz): Boolean
        if (quizLogRepository.existsByUserAndQuiz(user, quiz)) {
            throw IllegalArgumentException("User ${userId} has already solved quiz ${quizId}")
        }

        // 5. QuizLog 엔티티 생성 및 저장
        val quizLog = QuizLog(
            quiz = quiz,             // 조회된 퀴즈 엔티티 연결
            user = user,             // 조회된 사용자 엔티티 연결
            option = selectedOption  // 조회된 선택 옵션 엔티티 연결
            // id, createdAt 필드는 DB 및 Auditing에 의해 자동 설정
        )

        quizLogRepository.save(quizLog)

        // (선택 사항) 양방향 관계를 객체 레벨에서도 설정 (즉시 최신 상태 반영을 위해)
        // quiz.quizLogs.add(savedQuizLog) // Quiz 엔티티에 List<QuizLog> 필드가 있다면
        // user.quizLogs.add(savedQuizLog) // User 엔티티에 List<QuizLog> 필드가 있다면

        // 메소드가 성공적으로 완료되면 트랜잭션이 커밋되고 DB에 반영됩니다.
    }
}