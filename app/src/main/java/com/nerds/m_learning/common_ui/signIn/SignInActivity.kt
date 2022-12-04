package com.nerds.m_learning.common_ui.signIn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.nerds.m_learning.R
import com.nerds.m_learning.common_ui.signUp.SignUpActivity
import com.nerds.m_learning.databinding.ActivitySignInBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.ui.activities.StudentContainerActivity
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeViewModel
import com.nerds.m_learning.teacher.ui.activities.TeacherContainerActivity
import com.nerds.m_learning.teacher.ui.fragments.home.HomeViewModel
import com.shasin.notificationbanner.Banner

class SignInActivity : AppCompatActivity() {

    /*----------------------------------*/

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    /*----------------------------------*/

    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    private val viewModel: SignInViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var studentHomeViewModel: StudentHomeViewModel

    /*----------------------------------*/

    private var mStudent: Students.Student? = null

    /*----------------------------------*/

    private val mTAG: String = "_SignInActivity"
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        /*----------------------------------*/

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("Fcm", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("Fcm", "onViewCreated: token -> $token ")
        })



        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        studentHomeViewModel = ViewModelProvider(this)[StudentHomeViewModel::class.java]
        supportActionBar?.hide()
        /*----------------------------------*/
        getDataFromDataStore()

        //region Student observe
        viewModel.studentLogin.observe(this) {
            if (it.data == true) {
                binding.progressBar.visibility = View.GONE

                Banner.make(
                    binding.root, this, Banner.SUCCESS,
                    "Student Login successfully :)", Banner.TOP, 2000
                ).show()
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                val rbID = binding.rbRadioBtn.checkedRadioButtonId.toString()

                // auth.currentUser?.uid!!

                val user = DataOfUser(email, password, rbID)

                dataStoreViewModel.saveToDataStore(user)

                Handler(Looper.getMainLooper()).postDelayed({
                    Intent(this, StudentContainerActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                }, 2000)

            } else {
                if (it.message != null) {
                    Banner.make(
                        binding.root, this, Banner.ERROR,
                        "${it.message} :(", Banner.TOP, 3000
                    ).show()
                } else {
                    Banner.make(
                        binding.root, this, Banner.ERROR,
                        "Not a student account :(", Banner.TOP, 3000
                    ).show()
                }
            }
        }
        //endregion

        //region Teacher observe
        viewModel.teacherLogin.observe(this) {
            if (it.data == true) {
                binding.progressBar.visibility = View.GONE
                Banner.make(
                    binding.root, this, Banner.SUCCESS,
                    "Teacher Login successfully :)", Banner.TOP, 2000
                ).show()
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                val rbID = binding.rbRadioBtn.checkedRadioButtonId.toString()

                val user = DataOfUser(email, password, rbID)

                dataStoreViewModel.saveToDataStore(user)

                Handler(Looper.getMainLooper()).postDelayed({
                    Intent(this, TeacherContainerActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                }, 2000)

            } else {
                if (it.message != null) {
                    Banner.make(
                        binding.root, this, Banner.ERROR,
                        "${it.message} :(", Banner.TOP, 3000
                    ).show()
                } else {
                    Banner.make(
                        binding.root, this, Banner.ERROR,
                        "Not a teacher account :(", Banner.TOP, 3000
                    ).show()
                }
            }
        }
        //endregion

        /*----------------------------------*//*----------------------------------*/
        /*----------------------------------*//*----------------------------------*/

        // region  UI Components

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            when {
                email.isEmpty() -> getString(R.string.filL_email)
                password.isEmpty() -> getString(R.string.fill_password)
                else -> {
                    signIn()

                }
            }
        }

        //endregion
        /*----------------------------------*/
    }

    private fun signIn() {
        val email = binding.edEmail.text.toString()
        val password = binding.edPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty() && (binding.rbStudent.isChecked || binding.rbTeacher.isChecked)) {

            when (binding.rbRadioBtn.checkedRadioButtonId) {


                binding.rbStudent.id -> {

                    viewModel.studentLogin(this, email, password)

                }
                binding.rbTeacher.id -> {
                    viewModel.teacherLogin(email, password)
                }
            }
        } else {
            Banner.make(
                binding.root, this, Banner.ERROR,
                "All Fields must be filled out first!! :(", Banner.TOP, 3000
            ).show()
        }
    }


    private fun getDataFromDataStore() {
        dataStoreViewModel.readFromDataStore.observe(this) {
            binding.edEmail.setText(it.email)
            binding.edPassword.setText(it.password)
            if (it.rbID != "") {
                binding.rbRadioBtn.check(it.rbID.toInt())
            }
        }
    }
}