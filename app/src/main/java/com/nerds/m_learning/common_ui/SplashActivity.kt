package com.nerds.m_learning.common_ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.common_ui.signIn.SignInActivity
import com.nerds.m_learning.common_ui.signIn.SignInViewModel
import com.nerds.m_learning.databinding.ActivitySplashBinding
import com.nerds.m_learning.student.ui.activities.StudentContainerActivity
import com.nerds.m_learning.teacher.ui.activities.TeacherContainerActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val userID = auth.currentUser?.uid

    /*----------------------------------*/
    private val viewModel: SignInViewModel by viewModels()

    /*----------------------------------*/
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        supportActionBar?.hide()


        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )

        binding.imgSplashLogo.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.splash_in
            )
        )


        Handler().postDelayed({
            binding.imgSplashLogo.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.splash_out
                )
            )
            Handler().postDelayed({

                if (userID != null) {
                    viewModel.studentLoginExist(userID)
                } else {
                    binding.imgSplashLogo.visibility = View.GONE
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                viewModel.studentLoginExist.observe(this) {
                    if (it.data != null) {
                        binding.imgSplashLogo.visibility = View.GONE
                        val intent = Intent(this, StudentContainerActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.imgSplashLogo.visibility = View.GONE
                        val intent = Intent(this, TeacherContainerActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }


                //   goToLoginActivity()
            }, 500)
        }, 900)


        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

    }

}