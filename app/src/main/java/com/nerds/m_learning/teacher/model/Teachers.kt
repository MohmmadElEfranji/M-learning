package com.nerds.m_learning.teacher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class Teachers {

    @Parcelize
    data class Teacher(
        var teacherID: String = "",
        var firstName: String = "",
        var middleName: String = "",
        var lastName: String = "",
        var address: String = "",
        var mobileNumber: String = "",
        var birthDate: Date? = Date(),
        var email: String? = "",
        var password: String? = "",
        var firstLogin: Boolean = false
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
        var search_key: String = ""
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
        var lastLecture: Boolean? = false
    ) : Parcelable

    @Parcelize
    data class AssignmentsOfLecture(
        var assignmentID: String = "",
        var lectureID: String = "",
        var teacherID: String = "",
        var courseID: String = "",
        var assignmentName: String? = null,
        var assignmentDescription: String? = null,
        var assignmentFile: String? = "",
        var assignmentFileName: String? = "",
        var visibility: Boolean? = true
    ) : Parcelable

    @Parcelize
    data class ChapterOfLecture(
        var chapterID: String = "",
        var lectureID: String = "",
        var teacherID: String = "",
        var courseID: String = "",
        var chapterName: String? = null,
        var chapterDescription: String? = null,
        var chapterFile: String? = "",
        var chapterFileName: String? = "",
        var visibility: Boolean? = true
    ) : Parcelable

    @Parcelize
    data class CourseStudents(
        var StudentID: String = "",
        var StudentEmail: String? = null,
        var courseID: String = "",
    ) : Parcelable

    @Parcelize
    data class ChatChannel(
        var chatChannelID: String = "",
        var userIds: MutableList<String> = ArrayList()
    ) : Parcelable

    @Parcelize
    data class ChatChannelForUser(
        var chatChannelID: String = "",
    ) : Parcelable

    data class PassCourseID(val courseID: String)
    data class PassID(val lectureID: String, val courseID: String)

    data class PassCourseData(val course: Courses)

    companion object {
        const val COLLECTION_TEACHERS = "Teachers"
        const val COLLECTION_ALL_COURSES = "AllCourses"
        const val COLLECTION_CHAT_CHANNEL = "ChatChannel"

        // SubCollection
        const val SUB_COLLECTION_COURSES = "Courses"
        const val SUB_COLLECTION_LECTURES = "Lectures"
        const val SUB_COLLECTION_COURSE_STUDENTS = "CourseStudents"
        const val SUB_COLLECTION_ASSIGNMENTS = "Assignments"
        const val SUB_COLLECTION_Submission_ASSIGNMENTS = "SubmissionAssignments"
        const val SUB_COLLECTION_CHAPTERS = "Chapters"
        const val SUB_COLLECTION_CHAT_CHANNEL = "EngagedChatChannel"

        // Path for storage
        const val CHILD_PATH_COURSES = "/Courses/Images"
        const val CHILD_PATH_Lectures = "/Courses/Lectures/videos"
        const val CHILD_PATH_ASSIGNMENT_OF_Lectures = "/Courses/Lectures/Assignments"
        const val CHILD_PATH_Submissions_ASSIGNMENT_OF_Lectures = "/Courses/Lectures/Assignments/Submissions"
        const val CHILD_PATH_CHAPTERS_OF_Lectures = "/Courses/Lectures/Chapters"
    }
}