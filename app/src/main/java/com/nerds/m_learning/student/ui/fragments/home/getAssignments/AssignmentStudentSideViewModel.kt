package com.nerds.m_learning.student.ui.fragments.home.getAssignments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class AssignmentStudentSideViewModel : ViewModel() {
    private val teachersFirebaseRepo = TeachersFirebaseRepo
    private val studentsFirebaseRepo = StudentsFirebaseRepo

    /*----------------------------------*/

    private val _addSubmissionAssignment = MutableLiveData<FirebaseResponse<Boolean>>()
    val addSubmissionAssignment: LiveData<FirebaseResponse<Boolean>> get() = _addSubmissionAssignment

    /*----------------------------------*/
    private val _allAssignments: MutableLiveData<FirebaseResponse<MutableList<Teachers.AssignmentsOfLecture>>> =
        MutableLiveData()
    val allAssignments: MutableLiveData<FirebaseResponse<MutableList<Teachers.AssignmentsOfLecture>>> get() = _allAssignments

    /*----------------------------------*/
    private val _assignment: MutableLiveData<FirebaseResponse<Teachers.AssignmentsOfLecture>> =
        MutableLiveData()
    val assignment: MutableLiveData<FirebaseResponse<Teachers.AssignmentsOfLecture>> get() = _assignment
    /*----------------------------------*/

    fun getAllAssignmentsOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ) {
        viewModelScope.launch {
            val result =
                studentsFirebaseRepo.getAllAssignmentsOfLecture(teacherID, courseID, lectureID)
            if (result.data != null) {
                _allAssignments.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allAssignments.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun getAssignmentsLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.getAssignmentOfLecture(
                    teacherID,
                    courseID,
                    lectureID,
                    assignmentID
                )
            if (result.data != null) {
                _assignment.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _assignment.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun addSubmissionAssignment(
        assignment: Students.SubmissionAssignmentsOfLecture,
        teacherID: String,
        studentID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String,
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.addSubmissionAssignment(
                assignment,
                teacherID,
                studentID,
                courseID,
                lectureID,
                assignmentID
            )
            if (result.data != null) {
                _addSubmissionAssignment.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addSubmissionAssignment.value = FirebaseResponse.Failure(null, result.message)
        }

    }
}