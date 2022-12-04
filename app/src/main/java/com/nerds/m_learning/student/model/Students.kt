package com.nerds.m_learning.student.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class Students {

    @Parcelize
    data class Student(
        var studentID: String = "",
        var firstName: String = "",
        var middleName: String = "",
        var lastName: String = "",
        var address: String = "",
        var mobileNumber: String = "",
        var birthDate: Date? = Date(),
        var email: String? = null,
        var password: String? = null,
        var interests: MutableList<String> = ArrayList(),
        var numberOfRegisteredCourses: Int = 0,
        var firstLogin: Boolean? = false,
    ) : Parcelable

    @Parcelize
    data class Courses(
        var courseID: String = "",
        var teacherID: String = "",
        var courseName: String? = null,
        var courseField: String? = null,
        var courseDescription: String? = null,
        var courseImage: String? = "",
        var courseNameImage: String? = "",
        var visibility: Boolean? = true,
        var publishing: Boolean? = false,
        var finished: Boolean? = false
    ) : Parcelable

    @Parcelize
    data class LecturesOfCourse(
        var lectureID: String = "",
        var courseID: String = "",
        var teacherID: String = "",
        var lectureName: String? = null,
        var lectureDescription: String? = null,
        var lectureVideo: String? = "",
        var lectureVideoName: String? = "",
        var visibility: Boolean? = true,
        var watched: Boolean? = false,
        var lastLecture: Boolean? = false
    ) : Parcelable

    @Parcelize
    data class SubmissionAssignmentsOfLecture(
        var studentID: String = "",
        var studentEmail: String? = "",
        var assignmentFile: String? = "",
        var assignmentFileName: String? = "",
    ) : Parcelable

    companion object {
        const val COLLECTION_STUDENTS = "Students"

        // SubCollection
        const val SUB_COLLECTION_COURSES = "Courses"
    }
}