package com.nerds.m_learning.student.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylibrary3.model.remote.RetrofitInstance
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.nerds.m_learning.R
import com.nerds.m_learning.common_ui.signIn.SignInActivity
import com.nerds.m_learning.databinding.FragmentHomeStudentBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.RegisteredCoursesAdapter
import com.nerds.m_learning.student.dialog.CustomDialogFragment
import com.nerds.m_learning.student.model.NotificationData
import com.nerds.m_learning.student.model.PushNotification
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.ui.fragments.chat.ChatPrivateStudentSideFragment
import com.nerds.m_learning.student.ui.fragments.explore.AllCoursesViewModel
import com.nerds.m_learning.student.ui.fragments.home.getLectures.LecturesStudentSideFragment
import com.nerds.m_learning.student.ui.fragments.home.getLectures.LecturesStudentSideViewModel
import com.nerds.m_learning.student.util.Constant
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


class StudentHomeFragment : Fragment(), RegisteredCoursesAdapter.OnCourseReceived {

    /*----------------------------------*/
    private val registeredCoursesAdapter: RegisteredCoursesAdapter by lazy {
        RegisteredCoursesAdapter(requireContext(), this)
    }
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    private val studentEmail = auth.currentUser!!.email
    private var countStudent: Int = 0
    private val collectionRef = Firebase.firestore.collection(Students.COLLECTION_STUDENTS)
    private val arrayOfInterests: MutableList<String> = ArrayList()

    /*----------------------------------*/
    var date: Date? = null

    private var st = false
    private val viewModel: StudentHomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val allCoursesViewModel: AllCoursesViewModel by activityViewModels()
    private val lecturesStudentSideViewModel: LecturesStudentSideViewModel by activityViewModels()
    private val m: MutableList<Boolean> = ArrayList()
    /*----------------------------------*/

    private val mTAG: String = "_StudentHomeFragment"
    private lateinit var binding: FragmentHomeStudentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_student, container, false)
        return binding.root
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/


        setUpRecycler()

        binding.btnMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.popup_menu3, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.action_logOut -> {
                        auth.signOut()
                        val intent = Intent(requireContext(), SignInActivity::class.java)
                        startActivity(intent)

                        requireActivity().finish()

                    }

                }
                true
            }

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
            //popupMenu.show()
        }

        /*----------------------------------*/
        viewModel.getAllCourses(studentID)

        viewModel.allCourses.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {
                registeredCoursesAdapter.submitList(task.data)
                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }

            } else {

                Log.d(mTAG, "StudentHomeFragment: allCourses => ${task.message}")
            }
        }
        /*----------------------------------*/

        viewModel.updateFirstLogin.observe(viewLifecycleOwner) {
            if (it.data == true) {
                viewModel.saveToDataStore("true")
            }
        }

        viewModel.readFromDataStore.observe(viewLifecycleOwner) { firstLogin ->
            if (firstLogin == "false") {
                viewModel.updateFirstLogin(studentID)
                val dialog = CustomDialogFragment()
                dialog.show(parentFragmentManager, dialog.tag)

            }
        }

        sharedViewModel.mSelectedFragment2.observe(viewLifecycleOwner) {
            viewModel.updateInterests(studentID, it)
        }

    }

    private fun setUpRecycler() {
        binding.rvRegisteredCourses.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRegisteredCourses.adapter = registeredCoursesAdapter

    }

    override fun onCourseReceived(passCourseID: Students.Courses) {
        sharedViewModel.publishID2(passCourseID)
        // findNavController().navigate(R.id.lecturesStudentSideFragment)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, LecturesStudentSideFragment()).commit()

        sharedViewModel.mSelectedFragment.value = 5
    }

    override fun onClick(course: Students.Courses, menu: View) {
        val popupMenu = PopupMenu(requireContext(), menu)
        popupMenu.menuInflater.inflate(R.menu.popup2_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->


            when (item.itemId) {
                R.id.action_delete -> {

                    lecturesStudentSideViewModel.getAllLecturesOfCourse(
                        studentID, course.courseID
                    )
                    lecturesStudentSideViewModel.allLectures.observe(viewLifecycleOwner) {
                        val mutableList = it.data!!

                        for (i in mutableList) {
                            viewModel.deleteLecturesOfCourse(
                                studentID,
                                course.courseID,
                                i.lectureID
                            )
                        }

                    }
                    viewModel.unsubscribeCourse(studentID, course)
                    viewModel.getAllCourses(studentID)
                    viewModel.unsubscribeCourse.observe(viewLifecycleOwner) {
                        if (it.data == true) {
                            Banner.make(
                                binding.root, requireActivity(), Banner.SUCCESS,
                                "Subscription has been canceled successfully :)", Banner.TOP, 2000
                            ).show()
                            date = Calendar.getInstance().time
                            val m = android.text.format.DateFormat.format("hh:mm a", date)
                            val title = "Subscriptions"
                            val message =
                                "You have unsubscribed from the ${course.courseName} course :)\n  " +
                                        "Time of unsubscribed :$m"

                            PushNotification(
                                NotificationData(title, message),
                                Constant.TOPIC_UNSUBSCRIBE
                            ).also { noty ->
                                sendNotification(noty)
                            }

                            sendNotificationEmail(message)
                            viewModel.unsubscribeCourse.removeObservers(viewLifecycleOwner)
                        } else {
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }
            true
        }
        popupMenu.show()
    }

    override fun onChatClick(course: Students.Courses) {

        val fragment = ChatPrivateStudentSideFragment()
        val arguments = Bundle()
        arguments.putParcelable("course", course)
        fragment.arguments = arguments
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainerView, fragment, "FRAGMENT_TAG").commit()

        sharedViewModel.mSelectedFragment.value = 5
    }

    override fun onOpen(
        course: Students.Courses,
        holder: RegisteredCoursesAdapter.ItemViewHolder,
        position: Int
    ) {

        val y = registeredCoursesAdapter.currentList
        val n: MutableList<Students.Courses> = ArrayList()
        val p = y.filter {
            it.finished == false
        }
        n.addAll(p)


        if (n.isNotEmpty()) {
            Log.d(mTAG, "onOpen: ${n.size}")
            viewModel.updateNumberOfRegisteredCourses(
                studentID,
                n.size
            )

            n.clear()
        }



        if (registeredCoursesAdapter.currentList[position].finished == true) {
            holder.binding.tvCourseName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

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
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

}