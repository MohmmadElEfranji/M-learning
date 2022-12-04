package com.nerds.m_learning.teacher.ui.fragments.lectures.add_lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class AddLectureViewModel:ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo
    /*----------------------------------*/
    private val _addLecture = MutableLiveData<FirebaseResponse<Boolean>>()
    val addLecture : LiveData<FirebaseResponse<Boolean>> get() = _addLecture
    /*----------------------------------*/

    fun addLecture(lecture: Teachers.LecturesOfCourse,teacherID:String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addLecture(lecture,teacherID)
            if (result.data != null) {
                _addLecture.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addLecture.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}