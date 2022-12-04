package com.nerds.m_learning.teacher.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.model.Teachers

class SharedViewModel : ViewModel() {

    private val _sharedCourseID: MutableLiveData<Teachers.PassCourseID> = MutableLiveData()
    val sharedCourseID: LiveData<Teachers.PassCourseID> = _sharedCourseID

    private val _sharedCourseID2: MutableLiveData<Students.Courses> = MutableLiveData()
    val sharedCourseID2: LiveData<Students.Courses> = _sharedCourseID2


    private val _sharedLectureID: MutableLiveData<Students.LecturesOfCourse> = MutableLiveData()
    val sharedLectureID: LiveData<Students.LecturesOfCourse> = _sharedLectureID

    private val _sharedChapterID: MutableLiveData<Teachers.ChapterOfLecture> = MutableLiveData()
    val sharedChapterID: LiveData<Teachers.ChapterOfLecture> = _sharedChapterID

    private val _sharedAssignmentID: MutableLiveData<Teachers.AssignmentsOfLecture> =
        MutableLiveData()
    val sharedAssignmentID: LiveData<Teachers.AssignmentsOfLecture> = _sharedAssignmentID

    /*----------------------------------*//*----------------------------------*/
    private val _sharedID: MutableLiveData<Teachers.PassID> = MutableLiveData()
    val sharedID: LiveData<Teachers.PassID> = _sharedID

    /*----------------------------------*//*----------------------------------*/
    private val _courseName: MutableLiveData<MutableList<String>> = MutableLiveData()
    val courseName: LiveData<MutableList<String>> = _courseName
    val mSelectedFragment: MutableLiveData<Int> = MutableLiveData()
    val mSelectedFragment2: MutableLiveData<MutableList<String>> = MutableLiveData()
    val mShareStudentID: MutableLiveData<String> = MutableLiveData()
    /*   private val _sharedSuggestedCourse: MutableLiveData<Teachers.PassCourseData> = MutableLiveData()
       val sharedSuggestedCourse: LiveData<Teachers.PassCourseData> = _sharedSuggestedCourse
   */

    fun publishID(passCourseID: Teachers.PassCourseID) {
        _sharedCourseID.value = passCourseID
    }

    fun publishID2(passCourseID: Students.Courses) {
        _sharedCourseID2.value = passCourseID
    }

    fun sharedAssignmentID(passCourseID: Teachers.AssignmentsOfLecture) {
        _sharedAssignmentID.value = passCourseID
    }

    fun publishLectureID(passCourseID: Students.LecturesOfCourse) {
        _sharedLectureID.value = passCourseID
    }

    fun sharedChapterID(passChapterID: Teachers.ChapterOfLecture) {
        _sharedChapterID.value = passChapterID
    }

    fun publishCourseID(courseID: Teachers.PassID, lectureID: Teachers.PassID) {
        _sharedID.value = courseID
        _sharedID.value = lectureID
    }


    fun publishCourseNameExist(courseName: MutableList<String>) {
        _courseName.value = courseName
    }

    /*fun publishData2(course: Teachers.PassCourseData) {
        _sharedSuggestedCourse.value = course

    }*/
}