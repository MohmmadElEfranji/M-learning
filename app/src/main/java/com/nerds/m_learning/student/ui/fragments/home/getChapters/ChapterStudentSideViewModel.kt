package com.nerds.m_learning.student.ui.fragments.home.getChapters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class ChapterStudentSideViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo
    private val studentsFirebaseRepo = StudentsFirebaseRepo

    private val _allChapters: MutableLiveData<FirebaseResponse<MutableList<Teachers.ChapterOfLecture>>> =
        MutableLiveData()
    val allChapters: MutableLiveData<FirebaseResponse<MutableList<Teachers.ChapterOfLecture>>> get() = _allChapters

    private val _chapter: MutableLiveData<FirebaseResponse<Teachers.ChapterOfLecture>> =
        MutableLiveData()
    val chapter: MutableLiveData<FirebaseResponse<Teachers.ChapterOfLecture>> get() = _chapter

    fun getAllChapterOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ) {
        viewModelScope.launch {
            val result =
                studentsFirebaseRepo.getAllChapterOfLecture(teacherID, courseID, lectureID)
            if (result.data != null) {
                _allChapters.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allChapters.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun getChapterOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
        chapterID: String
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.getChapterOfLecture(teacherID, courseID, lectureID, chapterID)
            if (result.data != null) {
                _chapter.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _chapter.value = FirebaseResponse.Failure(null, result.message)
        }

    }
}