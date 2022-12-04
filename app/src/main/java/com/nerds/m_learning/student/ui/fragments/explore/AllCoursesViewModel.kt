package com.nerds.m_learning.student.ui.fragments.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.NotificationEmail
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllCoursesViewModel : ViewModel() {

    private val studentsFirebaseRepo = StudentsFirebaseRepo

    private val _allCourses: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> =
        MutableLiveData()

    val allCourses: LiveData<FirebaseResponse<MutableList<Teachers.Courses>>> get() = _allCourses

    private val _allCoursesSearchWithFilter: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> =
        MutableLiveData()

    val allCoursesSearchWithFilter: LiveData<FirebaseResponse<MutableList<Teachers.Courses>>> get() = _allCoursesSearchWithFilter

    private val _allCoursesSearchWithOutFilter: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> =
        MutableLiveData()

    val allCoursesSearchWithOutFilter: LiveData<FirebaseResponse<MutableList<Teachers.Courses>>> get() = _allCoursesSearchWithOutFilter


    private val _allCourses2: MutableLiveData<FirebaseResponse<MutableList<Teachers.Courses>>> =
        MutableLiveData()

    val allCourses2: LiveData<FirebaseResponse<MutableList<Teachers.Courses>>> get() = _allCourses2

    private val _sendNotificationEmail: MutableLiveData<FirebaseResponse<Boolean>> =
        MutableLiveData()

    val sendNotificationEmail: LiveData<FirebaseResponse<Boolean>> get() = _sendNotificationEmail

    fun getAllCoursesWithFilter(courseID: MutableList<String>) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllCoursesWithFilter(courseID)
            if (result.data != null) {
                _allCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCourses.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun getAllCourseSearchWithFilter(text: String?, courseID: MutableList<String>) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllCourseSearchWithFilter(text, courseID)
            if (result.data != null) {
                _allCoursesSearchWithFilter.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCoursesSearchWithFilter.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun getAllCoursesWithOutFilter() {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllCoursesWithOutFilter()
            if (result.data != null) {
                _allCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCourses.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun getAllCourseSearchWithOutFilter(text: String?) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllCourseSearchWithOutFilter(text)
            if (result.data != null) {
                _allCoursesSearchWithOutFilter.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCoursesSearchWithOutFilter.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun sendNotificationEmail(  receiverEmail: String,
                                subject: String,
                                body: String,) {
        viewModelScope.launch{
            val result = studentsFirebaseRepo.sendNotificationEmail(receiverEmail,subject, body)
            if (result.data != null) {
                _sendNotificationEmail.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _sendNotificationEmail.value = FirebaseResponse.Failure(null, result.message)
        }

    }

}