package com.example.todolist.service

import com.example.todolist.domain.ClassRoom
import com.example.todolist.domain.User
import com.example.todolist.domain.UserClassRoom
import com.example.todolist.domain.UserRole
import com.example.todolist.repository.ClassRoomRepository
import com.example.todolist.repository.UserClassRoomRepository
import com.example.todolist.repository.UsersRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ClassServiceImpl(
    private val classroomRepository: ClassRoomRepository,
    private val usersRepository: UsersRepository,
    private val userClassroomRepository: UserClassRoomRepository
) : ClassService {
    @Transactional
    override fun enterClassByStudent(className: String, teacherName: String, studentId: String): ClassRoom {
        val classRoom = classroomRepository
            .findByClassNameAndTeacherNameAndRoleTeacher(className, teacherName)
            .orElseThrow { throw IllegalArgumentException("There is No Class") }

        val student = usersRepository.findById(studentId)
            .orElseThrow {NoSuchElementException("User ID $studentId not found") }
        val existingMembership = userClassroomRepository
            .findStudentClassRoomByUserAndClassName(student, classRoom)

        // 5. 멤버십이 없을 경우에만 생성 및 저장
        if (existingMembership.isEmpty) { // 명시적인 if 문 사용
            val newUserClassRoom = UserClassRoom(
                user = student, // !!! 학생 객체로 수정 !!!
                classRoom = classRoom,
                role = UserRole.STUDENT, // 학생 역할
                accessedAt = LocalDate.now()
            )
            // 저장 시 Unique Constraint 위반 가능성 (매우 드물지만)
            // DataIntegrityViolationException 처리가 필요할 수도 있습니다.
            userClassroomRepository.save(newUserClassRoom)
        } else{
            existingMembership.get().accessedAt = LocalDate.now()
        }

        return classRoom
    }

    @Transactional
    override fun enterClassByTeacher(className: String, teacherId: String): ClassRoom {
        // 1. 사용자 ID로 사용자 조회 (없으면 예외 발생)
        val teacher = usersRepository.findById(teacherId).orElseThrow {
            NoSuchElementException("User ID $teacherId not found")
        }

        // 2. 해당 선생님이 이미 이 이름의 클래스를 TEACHER 역할로 가지고 있는지 확인
        val existingTeacherClassRoom = userClassroomRepository
            .findTeacherClassRoomByUserAndClassName(teacher, className)

        // 3. 이미 존재하는 관계(클래스)가 있다면 해당 클래스 반환
        if (existingTeacherClassRoom.isPresent) {
            // 기존 관계에서 클래스룸을 가져와 반환
            return existingTeacherClassRoom.get().classRoom
        }

        // 4. ClassRoom이 존재하지 않으면 새로 생성
        val newClassRoom = ClassRoom(name = className)
        val newUserClassRoom = UserClassRoom(
            classRoom = newClassRoom,
            accessedAt = LocalDate.now(),
            user = teacher,
            role = UserRole.TEACHER
        )

        // 5. 새로 생성된 ClassRoom 저장
        classroomRepository.save(newClassRoom)
        userClassroomRepository.save(newUserClassRoom)

        // 6. 저장된 ClassRoom 반환
        return newClassRoom
    }

    override fun findStudentsInClassRoom(classId: Long): List<User> {
        val classroom = classroomRepository.findById(classId).orElseThrow {
            throw  IllegalArgumentException("There is no class")
        }
        return  usersRepository.findStudentsByClassRoom(classRoom = classroom)
    }
}
