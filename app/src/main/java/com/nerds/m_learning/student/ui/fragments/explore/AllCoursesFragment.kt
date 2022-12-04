package com.nerds.m_learning.student.ui.fragments.explore

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mylibrary3.model.remote.RetrofitInstance
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentAllCoursesBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.AllCoursesAdapter
import com.nerds.m_learning.student.model.NotificationData
import com.nerds.m_learning.student.model.PushNotification
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.ui.fragments.SearchFragment
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeViewModel
import com.nerds.m_learning.student.util.Constant.Companion.TOPIC
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class AllCoursesFragment : Fragment() {
    /*----------------------------------*/

    private val allCoursesAdapter: AllCoursesAdapter by lazy {
        AllCoursesAdapter(requireContext())
    }
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    private val studentEmail = auth.currentUser!!.email
    var date: Date? = null
    private lateinit var analytics: FirebaseAnalytics
    private var newArray = java.util.ArrayList<Teachers.Courses>()
    private var count: Int = 0
    private var countStudent: Int = 0
    /*----------------------------------*/

    var actionMode: ActionMode? = null
    private var actionModeCallback: ActionMode.Callback? = null

    /*----------------------------------*/
    private lateinit var viewModel: AllCoursesViewModel
    private val studentHomeViewModel: StudentHomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /*----------------------------------*/
    private val mTAG = "_AllCoursesFragment"
    private lateinit var binding: FragmentAllCoursesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_all_courses, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/
        setUpRecycler()


        viewModel = ViewModelProvider(this)[AllCoursesViewModel::class.java]


        // region AdMob

        /*MobileAds.initialize(requireContext()) {

        }

        val adView = AdView(requireContext())

        adView.adSize = AdSize.BANNER

        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"*/

        // endregion

        setupActionModeCallback()

        binding.edSearch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SearchFragment()).commit()
        }

        /*----------------------------------*/
        studentHomeViewModel.getAllCourses(studentID)
        studentHomeViewModel.checkNumberOfRegisteredCourses(studentID)
        /*----------------------------------*/
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        /*----------------------------------*/
        viewModel.getAllCoursesWithOutFilter()
        viewModel.allCourses.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {

                val array = task.data.distinct()
                //  newArray.addAll(task.data)
                if (array.isNotEmpty()) {
                    allCoursesAdapter.setCoursesList(array as MutableList<Teachers.Courses>)
                }

                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "AllCoursesFragment: getAllCourses => ${task.message}")
            }
        }

        /*----------------------------------*/

        allCoursesAdapter.setItemClick(object : AllCoursesAdapter.OnItemClick {

            override fun onItemClick(view: View?, allCourses: Teachers.Courses, position: Int) {
                if (allCoursesAdapter.selectedItemCount() > 0) {
                    toggleActionBar(position, allCourses)

                } else {

                    Toast.makeText(
                        requireContext(),
                        "clicked " + allCourses.courseName,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onLongPress(view: View?, allCourses: Teachers.Courses, position: Int) {
                toggleActionBar(position, allCourses)
            }
        })
        /*----------------------------------*/
    }

    override fun onPause() {
        super.onPause()
        if (actionMode != null) {
            actionMode!!.finish()
        }

    }

    private fun setupActionModeCallback() {
        actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.actions_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.item_doneSelect -> {
                        for (i in allCoursesAdapter.dataOfCourses) {
                            val studentCourses =
                                Students.Courses(
                                    i.courseID,
                                    i.teacherID,
                                    i.courseName,
                                    i.courseField,
                                    i.courseDescription,
                                    i.courseImage,
                                    i.courseNameImage
                                )

                            val courseStudents =
                                Teachers.CourseStudents(studentID, studentEmail, i.courseID)
                            studentHomeViewModel.addCourse(
                                studentCourses,
                                studentID,
                                courseStudents
                            )


                            studentHomeViewModel.checkNumberOfRegisteredCourses.observe(
                                viewLifecycleOwner
                            ) { task ->
                                if (task.data != null) {
                                    val numberOfRegisteredCourses =
                                        task.data.numberOfRegisteredCourses
                                    val newNumber = count + numberOfRegisteredCourses

                                    studentHomeViewModel.updateNumberOfRegisteredCourses(
                                        studentID,
                                        newNumber
                                    )
                                    date = Calendar.getInstance().time
                                    val m = android.text.format.DateFormat.format("hh:mm a", date)
                                    val title = "Subscriptions"
                                    val message =
                                        "You have successfully subscribed to the ${i.courseName} course :)\n  " +
                                                "Time of registration :$m"

                                    PushNotification(
                                        NotificationData(title, message),
                                        TOPIC

                                    ).also {
                                        sendNotification(it)
                                    }
                                    sendNotificationEmail(message)

                                    val params = Bundle()
                                    params.putString("course", i.courseName)
                                    analytics.logEvent("courses_registered", params)

                                    sharedViewModel.mSelectedFragment.value = 2

                                } else {
                                    Log.d(mTAG, "onActionItemClicked: ${task.message}")
                                }
                            }


                        }


                        mode?.finish()
                        return true
                    }
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                allCoursesAdapter.clearSelection()
                allCoursesAdapter.dataOfCourses.clear()
                actionMode = null

            }
        }
    }

    private fun setUpRecycler() {
        /*     binding.rvAllCourses.layoutManager = GridLayoutManager(requireActivity(), 2)
             binding.rvAllCourses.adapter = allCoursesAdapter
     */
        binding.rvAllCourses.apply {
            this.adapter = allCoursesAdapter
            this.layoutManager = GridLayoutManager(requireActivity(), 2)
            /*this.layoutManager =
                LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false);*/
            this.hasFixedSize()
        }
    }

    // toggling action bar that will change the color and option
    private fun toggleActionBar(position: Int, allCourses: Teachers.Courses) {
        if (actionMode == null) {
            actionMode =
                (activity as AppCompatActivity?)!!.startSupportActionMode(actionModeCallback!!)
        }

        toggleSelection(position, allCourses)

    }


    private fun toggleSelection(position: Int, allCourses: Teachers.Courses) {

        allCoursesAdapter.toggleSelection(position, allCourses)

        Log.d(mTAG, "toggleSelection2 =>: $position")
        count = allCoursesAdapter.selectedItemCount()

        studentHomeViewModel.checkNumberOfRegisteredCourses.observe(viewLifecycleOwner) {

            countStudent = it.data!!.numberOfRegisteredCourses

            //Log.d(mTAG, "onViewCreated: checkNumberOfRegisteredCourses => $countStudent")
            studentHomeViewModel.allCourses.observe(viewLifecycleOwner) { task ->
                if (task.data != null) {
                    val op = task.data
                    for (i in op) {
                        if (allCourses.courseID == i.courseID) {
                            if (actionMode != null) {
                                actionMode!!.finish()
                                Toast.makeText(
                                    requireContext(),
                                    "${allCourses.courseName} is subscribed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                } else {
                    Log.d(mTAG, "StudentHomeFragment: allCourses => ${task.message}")
                }
            }

            when {
                countStudent >= 5 -> {
                    Toast.makeText(requireContext(), "Can not select", Toast.LENGTH_SHORT).show()
                    actionMode!!.finish()
                    allCoursesAdapter.dataOfCourses.clear()

                }
                count == (6 - countStudent) || count == 0 -> {
                    Toast.makeText(requireContext(), "Can not select more", Toast.LENGTH_SHORT)
                        .show()
                    actionMode!!.finish()
                    allCoursesAdapter.dataOfCourses.clear()


                }
                else -> {
                    actionMode!!.title = count.toString()
                    actionMode!!.invalidate()
                }
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