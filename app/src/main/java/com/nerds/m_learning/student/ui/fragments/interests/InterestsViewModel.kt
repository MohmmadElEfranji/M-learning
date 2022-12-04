package com.nerds.m_learning.student.ui.fragments.interests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import kotlinx.coroutines.launch

class InterestsViewModel: ViewModel() {
    private val studentsFirebaseRepo = StudentsFirebaseRepo

    private val _allCourses: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> =
        MutableLiveData()

    val allCourses: LiveData<FirebaseResponse<MutableList<Teachers.Courses>>> get() = _allCourses



    fun getAllCoursesInterests(courseField: MutableList<String>) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllCoursesInterests(courseField)
            if (result.data != null) {
                _allCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCourses.value = FirebaseResponse.Failure(null, result.message)
        }
    }





}