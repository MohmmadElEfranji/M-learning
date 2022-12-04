package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.add_assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class AddAssignmentViewModel : ViewModel() {
    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _addAssignment = MutableLiveData<FirebaseResponse<Boolean>>()
    val addAssignment: LiveData<FirebaseResponse<Boolean>> get() = _addAssignment
    /*----------------------------------*/

    fun addAssignment(
        assignment: Teachers.AssignmentsOfLecture,
        teacherID: String,
        courseID: String,
        lectureID:String
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addAssignment(assignment, teacherID, courseID,lectureID)
            if (result.data != null) {
                _addAssignment.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addAssignment.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}

