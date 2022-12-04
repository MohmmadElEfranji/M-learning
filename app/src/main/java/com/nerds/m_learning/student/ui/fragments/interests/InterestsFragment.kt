package com.nerds.m_learning.student.ui.fragments.interests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mylibrary3.model.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentInterestsBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.InterestsAdapter
import com.nerds.m_learning.student.dialog.InterestsDialogFragment
import com.nerds.m_learning.student.model.NotificationData
import com.nerds.m_learning.student.model.PushNotification
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.ui.fragments.explore.AllCoursesViewModel
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeViewModel
import com.nerds.m_learning.student.util.Constant
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class InterestsFragment : Fragment(), InterestsAdapter.OnClickCourse {
    /*----------------------------------*/
    private val interestsAdapter: InterestsAdapter by lazy {
        InterestsAdapter(requireContext(), this, this)
    }
    var date: Date? = null

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    private val studentEmail = auth.currentUser!!.email
    private var countStudent: Int = 0

    /*----------------------------------*/
    private lateinit var viewModel: InterestsViewModel
    private lateinit var studentHomeViewModel: StudentHomeViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val allCoursesViewModel: AllCoursesViewModel by activityViewModels()
    private val collectionRef = Firebase.firestore.collection(Students.COLLECTION_STUDENTS)

    /*----------------------------------*/
    private val mTAG = "_InterestsFragment"
    private lateinit var binding: FragmentInterestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_interests, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/

        binding.swipeContainer.setOnRefreshListener {

            studentHomeViewModel.getAllTheNamesOfTheCoursesInterestedIn(studentID)
            binding.swipeContainer.isRefreshing = false
        }
        binding.swipeContainer.setColorSchemeResources(R.color.new_color)

        setUpRecycler()
        viewModel = ViewModelProvider(this)[InterestsViewModel::class.java]
        studentHomeViewModel = ViewModelProvider(this)[StudentHomeViewModel::class.java]

        /*----------------------------------*/
        studentHomeViewModel.getAllTheNamesOfTheCoursesInterestedIn(studentID)
        studentHomeViewModel.checkNumberOfRegisteredCourses(studentID)
        /*----------------------------------*/

        studentHomeViewModel.getAllTheNamesOfTheCoursesInterestedIn.observe(viewLifecycleOwner) {
            val mInterests = it.data!!.interests
            viewModel.getAllCoursesInterests(mInterests)

        }

        viewModel.allCourses.observe(viewLifecycleOwner) {
            if (it.data != null) {
                interestsAdapter.submitList(it.data)
                if (it.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (it.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "onViewCreated: allCourses Error => ${it.message} ")
            }
        }

        /*----------------------------------*/

        studentHomeViewModel.getAllCourses(studentID)


        /*----------------------------------*/

        binding.tvChangeInterests.setOnClickListener {
            val dialog = InterestsDialogFragment()
            dialog.show(parentFragmentManager, dialog.tag)
        }


    }

    private fun setUpRecycler() {
        binding.rvAllCoursesInterested.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvAllCoursesInterested.adapter = interestsAdapter
    }

    override fun onClickCourse(passCourse: Teachers.Courses) {
        collectionRef.document(studentID).get().addOnSuccessListener { document ->
            if (document != null) {
                val numberOfRegisteredCourses =
                    document.get("numberOfRegisteredCourses").toString().toInt()
                countStudent = numberOfRegisteredCourses
                if (countStudent in 0 until 5) {
                    val studentCourses =
                        Students.Courses(
                            passCourse.courseID,
                            passCourse.teacherID,
                            passCourse.courseName,
                            passCourse.courseField,
                            passCourse.courseDescription,
                            passCourse.courseImage,
                            passCourse.courseNameImage

                        )

                    val courseStudents =
                        Teachers.CourseStudents(studentID, studentEmail)
                    studentHomeViewModel.addCourse(
                        studentCourses,
                        studentID,
                        courseStudents
                    )
                    val newNumber = countStudent + 1

                    studentHomeViewModel.updateNumberOfRegisteredCourses(
                        studentID,
                        newNumber
                    )
                    date = Calendar.getInstance().time
                    val m = android.text.format.DateFormat.format("hh:mm a", date)
                    val title = "Subscriptions"
                    val message =
                        "You have successfully subscribed to the ${passCourse.courseName} course :)\n  " +
                                "Time of registration :$m"

                    PushNotification(
                        NotificationData(title, message),
                        Constant.TOPIC

                    ).also {
                        sendNotification(it)
                    }
                    sendNotificationEmail(message)
                    sharedViewModel.mSelectedFragment.value = 2

                } else {
                    Toast.makeText(requireContext(), "Can  Not select", Toast.LENGTH_SHORT).show()
                }

            } else {
                Log.d(mTAG, "No such document")
            }
        }

    }

    override fun onOpen(
        passCourse: Teachers.Courses,
        position: Int,
        holder: InterestsAdapter.ItemViewHolder
    ) {
        // Toast.makeText(requireContext(), "hi ${passCourse.courseID} "+ "$position", Toast.LENGTH_SHORT).show()

        studentHomeViewModel.allCourses.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {
                val op = task.data
                for (i in op) {
                    if (passCourse.courseID == i.courseID) {
                        holder.binding.btnSubscribe.text = "SUBSCRIBED"
                        holder.binding.btnSubscribe.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.gray
                            )
                        )
                        holder.binding.btnSubscribe.isClickable = false
                    }
                }
            } else {
                Log.d(mTAG, "StudentHomeFragment: allCourses => ${task.message}")
            }
        }


    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.pushNotification(notification)
                if (response.isSuccessful) {
                    Log.d("AddBookFragment", "Response isSuccessful }")
                } else {
                    Log.e("AddBookFragment", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("AddBookFragment", e.toString())
            }

        }

    private fun sendNotificationEmail(message: String) {
        try {
            val stringSenderEmail = "nerds2023@gmail.com"
            val stringReceiverEmail = studentEmail
            val stringPasswordSenderEmail = "bdauvnlvsahebvqz"

            val stringHost = "smtp.gmail.com"

            val properties: Properties = System.getProperties()

            properties["mail.smtp.host"] = stringHost
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"


            val session: Session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
                }
            })

            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(stringReceiverEmail))
            mimeMessage.subject = "Subscriptions"
            mimeMessage.setText(message)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }

        } catch (e: AddressException) {
            e.printStackTrace();
        } catch (e: MessagingException) {
            e.printStackTrace();
        }
    }
}