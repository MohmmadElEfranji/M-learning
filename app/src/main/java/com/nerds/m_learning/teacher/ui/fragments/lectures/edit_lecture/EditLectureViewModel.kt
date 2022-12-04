package com.nerds.m_learning.teacher.ui.fragments.lectures.edit_lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class EditLectureViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _editLecture = MutableLiveData<FirebaseResponse<Boolean>>()
    val editLecture: LiveData<FirebaseResponse<Boolean>> get() = _editLecture

    /*----------------------------------*/
    fun editCourse(
        newLectureMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.editLecture(newLectureMap, teacherID, courseID, lectureID)
            if (result.data != null) {
                _editLecture.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _editLecture.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}