package com.nerds.m_learning.common_ui.signIn

import android.app.Application
import androidx.lifecycle.*
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.repository.DataStoreRepository
import com.nerds.m_learning.teacher.model.Teachers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DataStoreRepository.getInstance(application)

    private var _student: MutableStateFlow<Students.Student?> = MutableStateFlow(Students.Student())
    val student: StateFlow<Students.Student?> = _student

    val readFromDataStore = repository.readFromDataStore.asLiveData()

 /*   fun getStudentFromDatastore() {
        viewModelScope.launch {
            _student.value = repository.readFromDataStore2()
        }
    }*/


    fun saveToDataStore(user:DataOfUser) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore(user)
    }
}