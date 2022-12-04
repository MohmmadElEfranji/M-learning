package com.nerds.m_learning.common_ui.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    /*----------------------------------*/
    private val studentsFirebaseRepo: StudentsFirebaseRepo = StudentsFirebaseRepo
    private val teachersFirebaseRepo: TeachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*//*----------------------------------*/
    private var _createStudentAccount: MutableLiveData<FirebaseResponse<Boolean>> =
        MutableLiveData()
    val createStudentAccount: LiveData<FirebaseResponse<Boolean>>
        get() = _createStudentAccount

    private var _createTeacherAccount: MutableLiveData<FirebaseResponse<Boolean>> =
        MutableLiveData()
    val createTeacherAccount: LiveData<FirebaseResponse<Boolean>>
        get() = _createTeacherAccount
    /*----------------------------------*//*----------------------------------*/
    fun signUpNewStudent(student: Students.Student) = viewModelScope.launch {
        val result = studentsFirebaseRepo.registerNewStudentAccount(student)
        if (result.data != null) {
            _createStudentAccount.value = FirebaseResponse.Success(result.data)
            return@launch
        }
        _createStudentAccount.value = FirebaseResponse.Failure(result.data, result.message)
    }
    /*----------------------------------*//*----------------------------------*/
    /*----------------------------------*//*----------------------------------*/
    fun signUpNewTeacher(teacher: Teachers.Teacher) = viewModelScope.launch {
        val result = teachersFirebaseRepo.registerNewTeacherAccount2(teacher)
        if (result.data != null) {
            _createTeacherAccount.value = FirebaseResponse.Success(result.data)
            return@launch
        }
        _createTeacherAccount.value = FirebaseResponse.Failure(result.data, result.message)
    }

}