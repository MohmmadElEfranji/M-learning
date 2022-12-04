package com.nerds.m_learning.student.ui.fragments.home.getLectures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class LecturesStudentSideViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo
    private val studentsFirebaseRepo = StudentsFirebaseRepo

    /*----------------------------------*/
    private val _allLectures: MutableLiveData<FirebaseResponse<MutableList<Students.LecturesOfCourse>>> =
        MutableLiveData()
    val allLectures: MutableLiveData<FirebaseResponse<MutableList<Students.LecturesOfCourse>>> get() = _allLectures
    /*----------------------------------*/

    private val _lecture: MutableLiveData<FirebaseResponse<Students.LecturesOfCourse>> =
        MutableLiveData()
    val lecture: MutableLiveData<FirebaseResponse<Students.LecturesOfCourse>> get() = _lecture

    private val _addLecture = MutableLiveData<FirebaseResponse<Boolean>>()
    val addLecture: LiveData<FirebaseResponse<Boolean>> get() = _addLecture

    /*----------------------------------*/
    /*----------------------------------*//*----------------------------------*/
    private val _updateLectureWatchedState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateLectureWatchedState: LiveData<FirebaseResponse<Boolean>> get() = _updateLectureWatchedState
    private val _updateCourseFinishedState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateCourseFinishedState: LiveData<FirebaseResponse<Boolean>> get() = _updateCourseFinishedState
    fun getAllLecturesOfCourse(studentID: String, courseID: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllLecturesOfCourse(studentID, courseID)
            if (result.data != null) {
                _allLectures.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allLectures.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun getLectureOfCourse(studentID: String, courseID: String, lectureID: String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.getLectureOfCourse(studentID, courseID, lectureID)
            if (result.data != null) {
                _lecture.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _lecture.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun updateLectureWatchedState(
        studentID: String,
        lecture: Students.LecturesOfCourse,
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.updateLectureWatchedState(studentID, lecture)
            if (result.data != null) {
                _updateLectureWatchedState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateLectureWatchedState.value = FirebaseResponse.Failure(null, result.message)
        }
    }


    fun updateCourseFinishedState(
        studentID: String,
        courseID:String,
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.updateCourseFinishedState(studentID, courseID)
            if (result.data != null) {
                _updateCourseFinishedState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateCourseFinishedState.value = FirebaseResponse.Failure(null, result.message)
        }
    }


    fun addLecture(
        lecture: Students.LecturesOfCourse,
        studentID: String
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addLectureInStudentsSide2(lecture, studentID)
            if (result.data != null) {
                _addLecture.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addLecture.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}