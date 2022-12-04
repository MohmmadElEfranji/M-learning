package com.nerds.m_learning.common_ui.signIn

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.DataStoreRepository
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel:ViewModel() {

    private var studentsFirebaseRepo: StudentsFirebaseRepo = StudentsFirebaseRepo
    private var teachersFirebaseRepo: TeachersFirebaseRepo = TeachersFirebaseRepo



    /*----------------------------------*//*----------------------------------*/

    private var _studentLogin: MutableLiveData<FirebaseResponse<Boolean>> =
        MutableLiveData()
    val studentLogin: LiveData<FirebaseResponse<Boolean>>
        get() = _studentLogin

    private var _studentLoginExist: MutableLiveData<FirebaseResponse<Students.Student>> =
        MutableLiveData()
    val studentLoginExist: LiveData<FirebaseResponse<Students.Student>>
        get() = _studentLoginExist

    /*----------------------------------*//*----------------------------------*/

    private var _teacherLogin: MutableLiveData<FirebaseResponse<Boolean>> =
        MutableLiveData()
    val teacherLogin: LiveData<FirebaseResponse<Boolean>>
        get() = _teacherLogin

    /*----------------------------------*//*----------------------------------*/

    fun studentLogin(context: Context, studentEmail: String, studentPassword: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.loginOnFirebaseAuth(context,studentEmail, studentPassword)
            if (result.data != null) {
                _studentLogin.value = FirebaseResponse.Success(result.data)

                return@launch
            }
            _studentLogin.value = FirebaseResponse.Failure(result.data, result.message)
        }
    }

    fun studentLoginExist(studentID: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.checkStudentAccountExists(studentID)
            if (result.data != null) {
                _studentLoginExist.value = FirebaseResponse.Success(result.data)

                return@launch
            }
            _studentLoginExist.value = FirebaseResponse.Failure(null, result.message)
        }
    }

    fun teacherLogin(teacherEmail: String,teacherPassword: String) = viewModelScope.launch {
        val result = teachersFirebaseRepo.loginOnFirebaseAuth(teacherEmail, teacherPassword)
        if (result.data != null) {
            _teacherLogin.value = FirebaseResponse.Success(result.data)
            return@launch
        }
        _teacherLogin.value = FirebaseResponse.Failure(result.data, result.message)
    }
}