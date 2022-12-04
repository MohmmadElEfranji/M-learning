package com.nerds.m_learning.teacher.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nerds.m_learning.R

class TeacherContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_container)

        supportActionBar?.hide()
    }
}