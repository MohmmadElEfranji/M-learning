package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.edit_assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class EditAssignmentViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _editAssignment = MutableLiveData<FirebaseResponse<Boolean>>()
    val editAssignment: LiveData<FirebaseResponse<Boolean>> get() = _editAssignment

    /*----------------------------------*/
    fun editAssignment(
        newAssignmentMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.editAssignment(
                    newAssignmentMap,
                    teacherID,
                    courseID,
                    lectureID,
                    assignmentID
                )
            if (result.data != null) {
                _editAssignment.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _editAssignment.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}