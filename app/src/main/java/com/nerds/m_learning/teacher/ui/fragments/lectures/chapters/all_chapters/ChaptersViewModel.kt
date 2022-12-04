package com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.all_chapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class ChaptersViewModel:ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _allChapters: MutableLiveData<FirebaseResponse<MutableList<Teachers.ChapterOfLecture>>> =
        MutableLiveData()
    val allChapters: MutableLiveData<FirebaseResponse<MutableList<Teachers.ChapterOfLecture>>> get() = _allChapters
    private val _updateChapterVisibilityState = MutableLiveData<FirebaseResponse<Boolean>>()
    val updateChapterVisibilityState: LiveData<FirebaseResponse<Boolean>> get() = _updateChapterVisibilityState
    /*----------------------------------*/
    private val _deleteChapter = MutableLiveData<FirebaseResponse<Boolean>>()
    val deleteChapter: LiveData<FirebaseResponse<Boolean>> get() = _deleteChapter

    /*----------------------------------*/
    fun getAllChapterOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.getAllChapterOfLecture(teacherID, courseID, lectureID)
            if (result.data != null) {
                _allChapters.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _allChapters.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun updateChapterVisibilityState(
        chapter: Teachers.ChapterOfLecture,
        visibility: Boolean
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.updateChapterVisibilityState(
                chapter,
                visibility
            )
            if (result.data != null) {
                _updateChapterVisibilityState.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _updateChapterVisibilityState.value = FirebaseResponse.Failure(null, result.message)
        }
    }
    /*----------------------------------*//*----------------------------------*/
    fun deleteChapter(
        teacherID: String,
        courseID: String,
        lectureID: String,
        chapterID: String,
        chapterFileName: String?
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.deleteChapter(
                teacherID,
                courseID,
                lectureID,
                chapterID,
                chapterFileName
            )
            if (result.data != null) {
                _deleteChapter.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _deleteChapter.value = FirebaseResponse.Failure(result.data, result.message)
        }
    }
}