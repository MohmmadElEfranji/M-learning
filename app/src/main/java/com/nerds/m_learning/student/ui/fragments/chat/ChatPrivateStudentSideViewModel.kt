package com.nerds.m_learning.student.ui.fragments.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.TextMessage
import com.nerds.m_learning.student.repository.StudentsFirebaseRepo
import com.nerds.m_learning.teacher.model.Teachers
import kotlinx.coroutines.launch

class ChatPrivateStudentSideViewModel : ViewModel() {

    private val studentsFirebaseRepo = StudentsFirebaseRepo

    private val _addOfChatChannel = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfChatChannel: LiveData<FirebaseResponse<Boolean>> get() = _addOfChatChannel

    private val _addOfMessageChannel = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfMessageChannel: LiveData<FirebaseResponse<Boolean>> get() = _addOfMessageChannel


    private val _addOfChatChannelForTeacher = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfChatChannelForTeacher: LiveData<FirebaseResponse<Boolean>> get() = _addOfChatChannelForTeacher

    private val _getOfChatChannelForTeacher =
        MutableLiveData<FirebaseResponse<Teachers.ChatChannelForUser>>()
    val getOfChatChannelForTeacher: LiveData<FirebaseResponse<Teachers.ChatChannelForUser>> get() = _getOfChatChannelForTeacher


    private val _addOfChatChannelForStudent = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfChatChannelForStudent: LiveData<FirebaseResponse<Boolean>> get() = _addOfChatChannelForStudent


    fun addOfChatChannel(chatChannelID: String, chatChannel: Teachers.ChatChannel) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.addOfChatChannel(chatChannelID, chatChannel)
            if (result.data != null) {
                _addOfChatChannel.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfChatChannel.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun addOfMessageChannel(
        chatChannelID: String,
        messages: TextMessage
    ) {
        viewModelScope.launch {
            val result = studentsFirebaseRepo.addOfMessageChannel(chatChannelID, messages)
            if (result.data != null) {
                _addOfMessageChannel.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfMessageChannel.value = FirebaseResponse.Failure(null, result.message)
        }

    }


    fun addChatChannelForTeacher(
        teacherID: String,
        courseID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ) {
        viewModelScope.launch {
            val result =
                studentsFirebaseRepo.addChatChannelForTeacher(teacherID, courseID, chatChannelID)
            if (result.data != null) {
                _addOfChatChannelForTeacher.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfChatChannelForTeacher.value =
                FirebaseResponse.Failure(result.data, result.message)
        }

    }


    fun addChatChannelForStudent(
        studentID: String,
        courseID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ) {
        viewModelScope.launch {
            val result =
                studentsFirebaseRepo.addChatChannelForStudent(studentID, courseID, chatChannelID)
            if (result.data != null) {
                _addOfChatChannelForStudent.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfChatChannelForStudent.value =
                FirebaseResponse.Failure(result.data, result.message)
        }

    }


    fun getChatChannelForStudent(
        studentID: String,
        courseID: String,
    ) {
        viewModelScope.launch {
            val result =
                studentsFirebaseRepo.getChatChannelForStudent(studentID, courseID)
            if (result.data != null) {
                _getOfChatChannelForTeacher.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _getOfChatChannelForTeacher.value =
                FirebaseResponse.Failure(null, result.message)
        }

    }
}