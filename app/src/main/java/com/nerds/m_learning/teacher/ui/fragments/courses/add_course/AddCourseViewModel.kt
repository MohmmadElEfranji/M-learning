package com.nerds.m_learning.teacher.ui.fragments.courses.add_course

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class AddCourseViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo
    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)
    private val collectionRefAllCourses = Firebase.firestore.collection(Teachers.COLLECTION_ALL_COURSES)

    /*----------------------------------*/
    private val _addCourse = MutableLiveData<FirebaseResponse<Boolean>>()
     val addCourse : LiveData<FirebaseResponse<Boolean>> get() = _addCourse

    private val _addCourseG = MutableLiveData<FirebaseResponse<Boolean>>()
    val addCourseG : LiveData<FirebaseResponse<Boolean>> get() = _addCourseG
    private val _addCourse2 = MutableLiveData<Exception?>()
    val addCourse2: LiveData<Exception?> get() = _addCourse2
    /*----------------------------------*/
    fun addCourse(course: Teachers.Courses) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addCourse(course)
            if (result.data != null) {
                _addCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addCourse.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }

    fun addOfAllCourse(course: Teachers.Courses) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addOfAllCourse(course)
            if (result.data != null) {
                _addCourseG.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addCourseG.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }

    /*----------------------------------*//*----------------------------------*/
    fun addCourse2(course: Teachers.Courses) {

        collectionRefTeachers.document(course.teacherID).collection(Teachers.SUB_COLLECTION_COURSES)
            .document(course.courseID).set(course).addOnSuccessListener {
                _addCourse2.value = null
            }.addOnFailureListener {
             _addCourse2.value = it
            }

    }
}