package com.nerds.m_learning.teacher.ui.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo
    /*----------------------------------*/
    private val _allCourses: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> =
        MutableLiveData()
    val allCourses: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> get() = _allCourses

    private val _allStudentsOfCourse: MutableLiveData<FirebaseResponse<MutableList<Teachers.CourseStudents>>> =
        MutableLiveData()
    val allStudentsOfCourse: MutableLiveData<FirebaseResponse<MutableList<Teachers.CourseStudents>>> get() = _allStudentsOfCourse
    /*----------------------------------*/
    private val _deleteCourse = MutableLiveData<FirebaseResponse<Boolean>>()
    val deleteCourse: LiveData<FirebaseResponse<Boolean>> get() = _deleteCourse

    private val _checkLoggedInState = MutableLiveData<FirebaseResponse<Teachers.Teacher>>()
    val checkLoggedInState: LiveData<FirebaseResponse<Teachers.Teacher>> get() = _checkLoggedInState


    private val _updateTeacherLoggedInState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateTeacherLoggedInState: LiveData<FirebaseResponse<Boolean>> get() = _updateTeacherLoggedInState
    private val _updateCourseVisibilityState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateCourseVisibilityState: LiveData<FirebaseResponse<Boolean>> get() = _updateCourseVisibilityState
    private val _updateCoursePublishingState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateCoursePublishingState: LiveData<FirebaseResponse<Boolean>> get() = _updateCoursePublishingState
/*----------------------------------*//*----------------------------------*/

    fun getAllCourses(teacherID: String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.getAllCourses(teacherID)
            if (result.data != null) {
                _allCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCourses.value = FirebaseResponse.Failure(null, result.message)
        }
    }

    fun getAllStudentOfCourse(teacherID: String,courseID: String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.getAllStudentsOfCourse(teacherID,courseID)
            if (result.data != null) {
                _allStudentsOfCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allStudentsOfCourse.value = FirebaseResponse.Failure(null, result.message)
        }
    }
/*----------------------------------*//*----------------------------------*/

    fun deleteCourse(teacherID: String, courseID: String,courseNameImage:String?) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.deleteCourse(teacherID, courseID,courseNameImage)
            if (result.data != null) {
                _deleteCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _deleteCourse.value = FirebaseResponse.Failure(result.data, result.message)
        }
    }
/*----------------------------------*//*----------------------------------*/

    fun checkLoggedInState(teacherID:String) {
    viewModelScope.launch {
        val result = teachersFirebaseRepo.checkLoggedInState(teacherID)
        if (result.data != null) {
            _checkLoggedInState.value = FirebaseResponse.Success(result.data)
            return@launch
        }
        _checkLoggedInState.value = FirebaseResponse.Failure(result.data, result.message)
    }
}


    fun updateTeacherLoggedInState(teacherID: String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateTeacherLoggedInState(teacherID)
            if (result.data != null) {
                _updateTeacherLoggedInState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateTeacherLoggedInState.value = FirebaseResponse.Failure(result.data, result.message)
        }
    }

    fun updateCourseVisibilityState(course: Teachers.Courses,visibility:Boolean) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateCourseVisibilityState(course,visibility)
            if (result.data != null) {
                _updateCourseVisibilityState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateCourseVisibilityState.value = FirebaseResponse.Failure(null, result.message)
        }
    }

    fun updateCoursePublishingState(course: Teachers.Courses) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateCoursePublishingState(course)
            if (result.data != null) {
                _updateCoursePublishingState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateCoursePublishingState.value = FirebaseResponse.Failure(null, result.message)
        }
    }
}