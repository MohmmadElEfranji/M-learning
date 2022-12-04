package com.nerds.m_learning.student.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.ActivityStudentContainerBinding
import com.nerds.m_learning.student.util.Constant
import com.nerds.m_learning.teacher.ui.SharedViewModel


class StudentContainerActivity : AppCompatActivity() {
    /*----------------------------------*/
    private val viewModel: SharedViewModel by viewModels()

    /*----------------------------------*/
    lateinit var navController: NavController

    /*----------------------------------*/
    private lateinit var binding: ActivityStudentContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student_container)


        supportActionBar?.hide()

        navController = findNavController(R.id.fragmentContainerView)


        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC)
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC_UNSUBSCRIBE)

        binding.navBottomNav.setupWithNavController(navController)
        viewModel.mSelectedFragment.observe(this) {
            when (it) {
                5 -> {
                    binding.navBottomNav.visibility = View.GONE
                }
                6 -> {
                    binding.navBottomNav.visibility = View.VISIBLE
                }
                2 -> {
                    binding.navBottomNav.selectedItemId = R.id.homeFragment2
                }
            }

        }


    }

}