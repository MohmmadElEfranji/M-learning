package com.nerds.m_learning.common_ui.signUp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.common_ui.signIn.SignInActivity
import com.nerds.m_learning.databinding.ActivitySignUpBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {

    /*----------------------------------*/
    private lateinit var analytics: FirebaseAnalytics

    private val viewModel: SignUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    /*----------------------------------*/
    private val arrayOfInterests: MutableList<String> = ArrayList()

    private val mTAG: String = "_SignUpActivity"
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        /*----------------------------------*/
        supportActionBar?.hide()

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics


        //region Student observe
        viewModel.createStudentAccount.observe(this) {
            if (it.data == true) {

                binding.progressBar.visibility = View.GONE

                Banner.make(
                    binding.root, this, Banner.SUCCESS,
                    "Student account created successfully :)", Banner.TOP, 2000
                ).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    goToSignInActivity()
                }, 2000)

            } else {
                Banner.make(
                    binding.root, this, Banner.ERROR,
                    "${it.message} :(", Banner.TOP, 3000
                ).show()
            }
        }
        //endregion

        //region Teacher observe
        viewModel.createTeacherAccount.observe(this) {
            if (it.data == true) {

                binding.progressBar.visibility = View.GONE

                Banner.make(
                    binding.root, this, Banner.SUCCESS,
                    "Teacher account created successfully :)", Banner.TOP, 2000
                ).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    goToSignInActivity()
                }, 2000)

            } else {
                Banner.make(
                    binding.root, this, Banner.ERROR,
                    "${it.message} :(", Banner.TOP, 3000
                ).show()
            }
        }

        //endregion

        /*----------------------------------*//*----------------------------------*/

        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            upDatable(calendar)
        }



        binding.edBirthDate.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.btnSignUP.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            checkFieldsThenSignUp()

            val params = Bundle()
            params.putString("SignUP", "urlPart")
            analytics.logEvent("my_login", params)

        }




        binding.tvAlreadyAccount.setOnClickListener {
            goToSignInActivity()
        }

        /*    binding.rbStudent.setOnClickListener {
                val dialog = CustomDialogFragment()
                dialog.show(supportFragmentManager, dialog.tag)
            }*/

        /*----------------------------------*/
    }

    //for DatePicker
    private fun upDatable(calendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.edBirthDate.setText(sdf.format(calendar.time))

    }

    private fun signUpNew(
        firstName: String,
        middleName: String,
        lastName: String,
        birthDate: String,
        address: String,
        mobileNumber: String,
        email: String,
        password: String,

        ) {

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val mBirthDate = formatter.parse(birthDate) as Date

        if ((binding.rbStudent.isChecked || binding.rbTeacher.isChecked)) {
            when (binding.rbAccountType.checkedRadioButtonId) {
                binding.rbStudent.id -> {

                    val student =
                        Students.Student(
                            studentID = "",
                            firstName = firstName,
                            middleName = middleName,
                            lastName = lastName,
                            address = address,
                            mobileNumber = mobileNumber,
                            birthDate = mBirthDate,
                            email = email,
                            password = password
                        )
                    viewModel.signUpNewStudent(student)

                    //analytics
                    analytics.setUserProperty("User_type", "Student")

                }
                binding.rbTeacher.id -> {
                    val teacher =
                        Teachers.Teacher(
                            teacherID = "",
                            firstName = firstName,
                            middleName = middleName,
                            lastName = lastName,
                            address = address,
                            mobileNumber = mobileNumber,
                            birthDate = mBirthDate,
                            email = email,
                            password = password
                        )
                    viewModel.signUpNewTeacher(teacher)

                    //analytics
                    analytics.setUserProperty("User_type", "Teacher")

                }
            }
        } else {
            Banner.make(
                binding.root, this, Banner.WARNING,
                "Please select an account type", Banner.TOP, 3000
            ).show()
        }

    }

    private fun goToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkFieldsThenSignUp() {
        val firstName = binding.edFirstName.text.toString().trim()
        val middleName = binding.edMiddleName.text.toString().trim()
        val lastName = binding.edLastName.text.toString().trim()
        val birthDate = binding.edBirthDate.text.toString().trim()
        val address = binding.edAddress.text.toString().trim()
        val mobileNumber = binding.edMobileNumber.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val confirmPassword = binding.edConfirmPassword.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()


        when {
            firstName.isEmpty() -> {
                binding.edFirstName.error = "Fill first name"
            }
            middleName.isEmpty() -> {
                binding.edMiddleName.error = "Fill middle name"
            }
            lastName.isEmpty() -> {
                binding.edLastName.error = "Fill last name"
            }
            birthDate.isEmpty() -> {
                binding.edBirthDate.error = "Fill birth date"
            }
            address.isEmpty() -> {
                binding.edAddress.error = "Fill address "
            }
            mobileNumber.isEmpty() -> {
                binding.edMobileNumber.error = "Fill mobile number"
            }
            email.isEmpty() -> {
                binding.edEmail.error = "Fill email "
            }
            confirmPassword.isEmpty() -> {
                binding.edConfirmPassword.error = "Fill confirm password"
            }
            password.isEmpty() -> {
                binding.edPassword.error = "Fill password"
            }
            confirmPassword != password -> {
                binding.edConfirmPassword.error = "Those passwords didn't match try again"
            }
            else -> {
                signUpNew(
                    firstName,
                    middleName,
                    lastName,
                    birthDate,
                    address,
                    mobileNumber,
                    email,
                    password
                )
            }
        }
    }

}