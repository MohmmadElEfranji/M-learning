package com.nerds.m_learning.teacher.ui.fragments.courses.edit_course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class EditCourseViewModel:ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo
    /*----------------------------------*/
    private val _editCourse = MutableLiveData<FirebaseResponse<Boolean>>()
    val editCourse : LiveData<FirebaseResponse<Boolean>> get() = _editCourse

    /*----------------------------------*/
    fun editCourse(newCourseMap: Map<String, Any>,
                   teacherID: String,
                   courseID: String,) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.editCourse(newCourseMap, teacherID, courseID)
            if (result.data != null) {
                _editCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _editCourse.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}