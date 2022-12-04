package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.get_assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class AssignmentViewModel : ViewModel() {
    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _allAssignments: MutableLiveData<FirebaseResponse<MutableList<Teachers.AssignmentsOfLecture>>> =
        MutableLiveData()
    val allAssignments: MutableLiveData<FirebaseResponse<MutableList<Teachers.AssignmentsOfLecture>>> get() = _allAssignments

    private val _allSubmissionOfAssignment: MutableLiveData<FirebaseResponse<MutableList<Students.SubmissionAssignmentsOfLecture>>> =
        MutableLiveData()
    val allSubmissionOfAssignment: MutableLiveData<FirebaseResponse<MutableList<Students.SubmissionAssignmentsOfLecture>>> get() = _allSubmissionOfAssignment

    private val _updateAssignmentVisibilityState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateAssignmentVisibilityState: LiveData<FirebaseResponse<Boolean>> get() = _updateAssignmentVisibilityState
    /*----------------------------------*/
    private val _deleteAssignment = MutableLiveData<FirebaseResponse<Boolean>>()
    val deleteAssignment: LiveData<FirebaseResponse<Boolean>> get() = _deleteAssignment

    /*----------------------------------*/
    fun getAllAssignmentsOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.getAllAssignmentsOfLecture(teacherID, courseID, lectureID)
            if (result.data != null) {
                _allAssignments.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allAssignments.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun getAllSubmissionOfAssignment(
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.getAllSubmissionOfAssignment(teacherID, courseID, lectureID,assignmentID)
            if (result.data != null) {
                _allSubmissionOfAssignment.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allSubmissionOfAssignment.value = FirebaseResponse.Failure(null, result.message)
        }

    }
    fun updateAssignmentVisibilityState(
        assignment: Teachers.AssignmentsOfLecture,
        visibility: Boolean
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateAssignmentVisibilityState(
                assignment,
                visibility
            )
            if (result.data != null) {
                _updateAssignmentVisibilityState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateAssignmentVisibilityState.value = FirebaseResponse.Failure(null, result.message)
        }
    }
    /*----------------------------------*//*----------------------------------*/
    fun deleteAssignment(
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String,
        assignmentFileName:String?
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.deleteAssignment(
                teacherID,
                courseID,
                lectureID,
                assignmentID,
                assignmentFileName
            )
            if (result.data != null) {
                _deleteAssignment.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _deleteAssignment.value = FirebaseResponse.Failure(result.data, result.message)
        }
    }

}