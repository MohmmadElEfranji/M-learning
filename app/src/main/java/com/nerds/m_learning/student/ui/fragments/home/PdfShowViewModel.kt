package com.nerds.m_learning.student.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

class PdfShowViewModel : ViewModel() {

    private val studentsFirebaseRepo = StudentsFirebaseRepo

    private var _url: MutableLiveData<InputStream> = MutableLiveData()

    val url: LiveData<InputStream>
        get() = _url

    fun getInputStreamFromURL(uri: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = studentsFirebaseRepo.getInputStreamFromURL(uri)
            result?.let {
                _url.postValue(it)
                return@launch
            }
        }
    }
}