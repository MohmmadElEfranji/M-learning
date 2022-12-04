package com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.edit_chapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class EditChapterViewModel:ViewModel() {
    private val teachersFirebaseRepo = TeachersFirebaseRepo

    /*----------------------------------*/
    private val _editChapter = MutableLiveData<FirebaseResponse<Boolean>>()
    val editChapter: LiveData<FirebaseResponse<Boolean>> get() = _editChapter

    /*----------------------------------*/
    fun editChapter(
        newChapterMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
        chapterID: String
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.editChapter(
                    newChapterMap,
                    teacherID,
                    courseID,
                    lectureID,
                    chapterID
                )
            if (result.data != null) {
                _editChapter.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _editChapter.value = FirebaseResponse.Failure(result.data, result.message)
        }

    }
}