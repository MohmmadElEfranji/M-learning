package com.nerds.m_learning.teacher.ui.fragments.lectures.get_lectures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class LecturesViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _allLectures: MutableLiveData<FirebaseResponse<MutableList<Teachers.LecturesOfCourse>>> =
        MutableLiveData()
    val allLectures: MutableLiveData<FirebaseResponse<MutableList<Teachers.LecturesOfCourse>>> get() = _allLectures

    /*----------------------------------*/

    private val _updateLectureVisibilityState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateLectureVisibilityState: LiveData<FirebaseResponse<Boolean>> get() = _updateLectureVisibilityState
    /*----------------------------------*/
    private val _updateLectureVisibilityStateFromStudents = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateLectureVisibilityStateFromStudents: LiveData<FirebaseResponse<Boolean>> get() = _updateLectureVisibilityStateFromStudents

    private val _deleteLecture = MutableLiveData<FirebaseResponse<Boolean>>()
    val deleteLecture: LiveData<FirebaseResponse<Boolean>> get() = _deleteLecture
    /*----------------------------------*//*----------------------------------*/

    fun getAllLecturesOfCourse(teacherID: String, courseID: String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.getAllLecturesOfCourse(teacherID, courseID)
            if (result.data != null) {
                _allLectures.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allLectures.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun updateLectureVisibilityState(
        lecture: Teachers.LecturesOfCourse,
        visibility: Boolean
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateLectureVisibilityState(
                lecture,
                visibility
            )
            if (result.data != null) {
                _updateLectureVisibilityState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateLectureVisibilityState.value = FirebaseResponse.Failure(null, result.message)
        }
    }

    fun updateLectureVisibilityStateFromStudents(
        studentID:String,
        lecture: Teachers.LecturesOfCourse,
        visibility: Boolean
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateLectureVisibilityStateFromStudents(
                studentID,
                lecture,
                visibility
            )
            if (result.data != null) {
                _updateLectureVisibilityState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateLectureVisibilityState.value = FirebaseResponse.Failure(null, result.message)
        }
    }

    /*----------------------------------*//*----------------------------------*/

    fun deleteLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
        lectureVideoName: String?
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.deleteLecture(teacherID, courseID, lectureID, lectureVideoName)
            if (result.data != null) {
                _deleteLecture.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _deleteLecture.value = FirebaseResponse.Failure(result.data, result.message)
        }
    }


}