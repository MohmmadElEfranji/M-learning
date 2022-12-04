package com.nerds.m_learning.student.ui.fragments.home

import android.app.Application
import androidx.lifecycle.*
import com.nerds.m_learning.common_ui.signIn.DataOfUser
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.DataStoreRepository
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentHomeViewModel (application: Application) : AndroidViewModel(application) {

    private val studentsFirebaseRepo = StudentsFirebaseRepo
    private val repository = DataStoreRepository.getInstance(application)


    val readFromDataStore = repository.readFromDataStore2.asLiveData()
    /*----------------------------------*/
    private val _checkNumberOfRegisteredCourses =
        MutableLiveData<FirebaseResponse<Students.Student>>()
    val checkNumberOfRegisteredCourses: LiveData<FirebaseResponse<Students.Student>> get() = _checkNumberOfRegisteredCourses
    private val _getAllTheNamesOfTheCoursesInterestedIn =
        MutableLiveData<FirebaseResponse<Students.Student>>()
    val getAllTheNamesOfTheCoursesInterestedIn: LiveData<FirebaseResponse<Students.Student>> get() = _getAllTheNamesOfTheCoursesInterestedIn

    private val _checkCourseRegisteredOnFireStore =
        MutableLiveData<FirebaseResponse<Teachers.Courses>>()
    val checkCourseRegisteredOnFireStore: LiveData<FirebaseResponse<Teachers.Courses>> get() = _checkCourseRegisteredOnFireStore

    /*----------------------------------*/
    private val _addCourse = MutableLiveData<FirebaseResponse<Boolean>>()
    val addCourse: LiveData<FirebaseResponse<Boolean>> get() = _addCourse

    /*----------------------------------*/
    private val _allCourses: MutableLiveData<FirebaseResponse<MutableList<Students.Courses>>> =
        MutableLiveData()
    val allCourses: MutableLiveData<FirebaseResponse<MutableList<Students.Courses>>> get() = _allCourses
    private val _unsubscribeCourse = MutableLiveData<FirebaseResponse<Boolean>>()
    val unsubscribeCourse: LiveData<FirebaseResponse<Boolean>> get() = _unsubscribeCourse

    private val _deleteLecturesOfCourse = MutableLiveData<FirebaseResponse<Boolean>>()
    val deleteLecturesOfCourse: LiveData<FirebaseResponse<Boolean>> get() = _deleteLecturesOfCourse
    /*----------------------------------*/
    private val _updateNumberOfRegisteredCourses = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateNumberOfRegisteredCourses: LiveData<FirebaseResponse<Boolean>> get() = _updateNumberOfRegisteredCourses

    private val _updateFirstLogin = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateFirstLogin: LiveData<FirebaseResponse<Boolean>> get() = _updateFirstLogin

    private val _updateInterests = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateInterests: LiveData<FirebaseResponse<Boolean>> get() = _updateInterests
    /*----------------------------------*//*----------------------------------*/
    fun checkNumberOfRegisteredCourses(studentID: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getDataOfStudent(studentID)
            if (result.data != null) {
                _checkNumberOfRegisteredCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _checkNumberOfRegisteredCourses.value =
                FirebaseResponse.Failure(result.data, result.message)
        }
    }

    fun getAllTheNamesOfTheCoursesInterestedIn(studentID: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getDataOfStudent(studentID)
            if (result.data != null) {
                _getAllTheNamesOfTheCoursesInterestedIn.value =
                    FirebaseResponse.Success(result.data)
                return@launch
            }
            _getAllTheNamesOfTheCoursesInterestedIn.value =
                FirebaseResponse.Failure(result.data, result.message)
        }
    }

    fun updateNumberOfRegisteredCourses(studentID: String, numberOfRegisteredCourses: Int) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.updateNumberOfRegisteredCourses(
                studentID,
                numberOfRegisteredCourses
            )
            if (result.data != null) {
                _updateNumberOfRegisteredCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateNumberOfRegisteredCourses.value =
                FirebaseResponse.Failure(result.data, result.message)
        }
    }

    fun updateFirstLogin(studentID: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.updateFirstLogin(studentID)
            if (result.data != null) {
                _updateFirstLogin.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateFirstLogin.value =
                FirebaseResponse.Failure(false, result.message)
        }
    }

    fun saveToDataStore(firstLogin:String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore2(firstLogin)
    }

    fun updateInterests(  studentID: String,
                          arrayOfInterests: MutableList<String>) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.updateInterests(studentID,arrayOfInterests)
            if (result.data != null) {
                _updateInterests.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateInterests.value =
                FirebaseResponse.Failure(false, result.message)
        }
    }
    /*----------------------------------*//*----------------------------------*/

    fun addCourse(
        course: Students.Courses,
        studentID: String,
        courseStudents: Teachers.CourseStudents
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.addCourse(course, studentID, courseStudents)
            if (result.data != null) {
                _addCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addCourse.value = FirebaseResponse.Failure(null, result.message)
        }
    }

    fun unsubscribeCourse(
        studentID: String,
        course: Students.Courses,
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.unsubscribeCourse(studentID, course)
            if (result.data != null) {
                _unsubscribeCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _unsubscribeCourse.value = FirebaseResponse.Failure(null, result.message)
        }
    }
    fun deleteLecturesOfCourse(
        studentID: String,
        courseID:String,
        lectureID:String,
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.deleteLecturesOfCourse(studentID, courseID,lectureID)
            if (result.data != null) {
                _deleteLecturesOfCourse.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _deleteLecturesOfCourse.value = FirebaseResponse.Failure(null, result.message)
        }
    }
    fun getAllCourses(studentID: String) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.getAllCoursesSubscribed(studentID)
            if (result.data != null) {
                _allCourses.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allCourses.value = FirebaseResponse.Failure(null, result.message)
        }
    }


}