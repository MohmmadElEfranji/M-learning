package com.nerds.m_learning.teacher.ui.fragments.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.TextMessage
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.repository.TeachersFirebaseRepo
import kotlinx.coroutines.launch

class ChatPrivateViewModel : ViewModel() {

    private val teachersFirebaseRepo = TeachersFirebaseRepo

    private val _addOfChatChannel = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfChatChannel: LiveData<FirebaseResponse<Boolean>> get() = _addOfChatChannel

    private val _addOfMessageChannel = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfMessageChannel: LiveData<FirebaseResponse<Boolean>> get() = _addOfMessageChannel

    private val _getOfMessageChannel = MutableLiveData<FirebaseResponse<MutableList<TextMessage>>>()
    val getOfMessageChannel: LiveData<FirebaseResponse<MutableList<TextMessage>>> get() = _getOfMessageChannel

    private val _addOfChatChannelForTeacher = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfChatChannelForTeacher: LiveData<FirebaseResponse<Boolean>> get() = _addOfChatChannelForTeacher

    private val _getOfChatChannelForTeacher =
        MutableLiveData<FirebaseResponse<Teachers.ChatChannelForUser>>()
    val getOfChatChannelForTeacher: LiveData<FirebaseResponse<Teachers.ChatChannelForUser>> get() = _getOfChatChannelForTeacher


    private val _addOfChatChannelForStudent = MutableLiveData<FirebaseResponse<Boolean>>()
    val addOfChatChannelForStudent: LiveData<FirebaseResponse<Boolean>> get() = _addOfChatChannelForStudent

    fun addOfChatChannel(chatChannelID: String, chatChannel: Teachers.ChatChannel) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.addOfChatChannel(chatChannelID, chatChannel)
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
            val result = teachersFirebaseRepo.addOfMessageChannel(chatChannelID, messages)
            if (result.data != null) {
                _addOfMessageChannel.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfMessageChannel.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun getOfMessageChannel(
        chatChannelID: String,
    ) {
        viewModelScope.launch {
            val result = teachersFirebaseRepo.getOfMessageChannel(chatChannelID)
            if (result.data != null) {
                _getOfMessageChannel.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _getOfMessageChannel.value = FirebaseResponse.Failure(null, result.message)
        }

    }

    fun addChatChannelForTeacher(
        teacherID: String,
        studentID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.addChatChannelForTeacher(teacherID, studentID, chatChannelID)
            if (result.data != null) {
                _addOfChatChannelForTeacher.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfChatChannelForTeacher.value =
                FirebaseResponse.Failure(result.data, result.message)
        }

    }

    fun getChatChannelForTeacher(
        teacherID: String,
        studentID: String,
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.getChatChannelForTeacher(teacherID, studentID)
            if (result.data != null) {
                _getOfChatChannelForTeacher.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _getOfChatChannelForTeacher.value =
                FirebaseResponse.Failure(null, result.message)
        }

    }

    fun addChatChannelForStudent(
        studentID: String,
        teacherID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ) {
        viewModelScope.launch {
            val result =
                teachersFirebaseRepo.addChatChannelForStudent(studentID, teacherID, chatChannelID)
            if (result.data != null) {
                _addOfChatChannelForStudent.value = FirebaseResponse.Success(result.data)
                return@launch
            }
            _addOfChatChannelForStudent.value =
                FirebaseResponse.Failure(result.data, result.message)
        }

    }
}