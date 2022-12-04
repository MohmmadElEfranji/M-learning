package com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.add_chapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class AddChapterViewModel: ViewModel() {
    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _addChapter = MutableLiveData<FirebaseResponse<Boolean>>()
    val addChapter: LiveData<FirebaseResponse<Boolean>> get() = _addChapter
    /*----------------------------------*/

    fun addChapter(
        chapter: Teachers.ChapterOfLecture,
        teacherID: String,
        courseID: String,
        lectureID: String,
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addChapter(chapter, teacherID, courseID,lectureID)
            if (result.data != null) {
                _addChapter.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addChapter.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}