package com.nerds.m_learning.teacher.ui.fragments.students_of_course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class CourseStudentsViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo

    private val _allStudentsOfCourse: MutableLiveData<FirebaseResponse<MutableList<Teachers.CourseStudents>>> =
        MutableLiveData()
    val allStudentsOfCourse: MutableLiveData<FirebaseResponse<MutableList<Teachers.CourseStudents>>> get() = _allStudentsOfCourse

    private val _updateCourseVisibilityState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateCourseVisibilityState: LiveData<FirebaseResponse<Boolean>> get() = _updateCourseVisibilityState

    fun getAllStudentsOfCourse(teacherID: String, courseID: String) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.getAllStudentsOfCourse(teacherID, courseID)
            if (result.data != null) {
                _allStudentsOfCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allStudentsOfCourse.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun updateCourseVisibilityStateFromStudents(
        studentID: String,
        course: Teachers.Courses,
        visibility: Boolean
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateCourseVisibilityStateFromStudents(
                studentID,
                course,
                visibility
            )
            if (result.data != null) {
                _updateCourseVisibilityState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateCourseVisibilityState.value = FirebaseResponse.Failure(null, result.message)
        }
    }
}