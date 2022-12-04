package com.nerds.m_learning.student.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylibrary3.model.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentSearchBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.SearchAdapter
import com.nerds.m_learning.student.model.LinearLayoutManagerWrapper
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


class SearchFragment : Fragment() {
    private val collectionRefAllCourses =
        Firebase.firestore.collection(Teachers.COLLECTION_ALL_COURSES)
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(requireContext())
    }
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    private val studentEmail = auth.currentUser!!.email
    var date: Date? = null


    private var count: Int = 0
    private var countStudent: Int = 0
    /*----------------------------------*/

    var actionMode: ActionMode? = null
    private var actionModeCallback: ActionMode.Callback? = null

    /*----------------------------------*/
    private lateinit var viewModel: AllCoursesViewModel
    private val studentHomeViewModel: StudentHomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var newArray = java.util.ArrayList<Teachers.Courses>()
    private var query: String? = null

    /*----------------------------------*/
    private val mTAG = "_SearchFragment"
    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/
        setUpRecycler()
        setHasOptionsMenu(true)
        getAllCourses()
        binding.edSearch.isFocusableInTouchMode = true
        binding.edSearch.isFocusable = true
        binding.edSearch.requestFocus()


        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val searchText: String = binding.edSearch.text.toString()

                searchAboutCourse(searchText.lowercase(Locale.getDefault()))

            }

        })
        viewModel = ViewModelProvider(this)[AllCoursesViewModel::class.java]

        setupActionModeCallback()

        /*----------------------------------*/
        studentHomeViewModel.getAllCourses(studentID)
        studentHomeViewModel.checkNumberOfRegisteredCourses(studentID)


        /*----------------------------------*/

        searchAdapter.setItemClick(object : SearchAdapter.OnItemClick {

            override fun onItemClick(view: View?, allCourses: Teachers.Courses, position: Int) {
                if (searchAdapter.selectedItemCount() > 0) {
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
                        for (i in searchAdapter.dataOfCourses) {
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
                                Teachers.CourseStudents(studentID, studentEmail)
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
                                    /* studentHomeViewModel.allCourses.observe(viewLifecycleOwner) {
                                         val studentArray = it.data
                                         val courseID: MutableList<String> = ArrayList()
                                         for (p in studentArray!!) {
                                             courseID.add(p.courseID)
                                         }
                                         viewModel.getAllCoursesWithFilter(courseID)

                                     }*/
                                    Banner.make(
                                        binding.root, requireActivity(), Banner.SUCCESS,
                                        "Subscribed successfully :)", Banner.TOP, 2000
                                    ).show()
                                    date = Calendar.getInstance().time
                                    val m = android.text.format.DateFormat.format("hh:mm a", date)
                                    val title = "Subscriptions"
                                    val message =
                                        "You have successfully subscribed to the ${i.courseName} course :)\n  " +
                                                "Time of registration :$m"

                                    PushNotification(
                                        NotificationData(title, message),
                                        Constant.TOPIC

                                    ).also {
                                        sendNotification(it)
                                    }
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
                searchAdapter.clearSelection()
                searchAdapter.dataOfCourses.clear()
                actionMode = null

            }
        }
    }

    private fun setUpRecycler() {
        binding.rvAllCourses.apply {
            this.adapter = searchAdapter
            //  this.layoutManager = GridLayoutManager(requireActivity(), 2)
            this.layoutManager =
                LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false);
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

        searchAdapter.toggleSelection(position, allCourses)

        Log.d(mTAG, "toggleSelection2 =>: $position")
        count = searchAdapter.selectedItemCount()

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
                    searchAdapter.dataOfCourses.clear()

                }
                count == (6 - countStudent) || count == 0 -> {
                    Toast.makeText(requireContext(), "Can not select more", Toast.LENGTH_SHORT)
                        .show()
                    actionMode!!.finish()
                    searchAdapter.dataOfCourses.clear()


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


    private fun searchAboutCourse(text: String?) {
        if (!text.isNullOrEmpty() && text.isNotBlank()) {

            collectionRefAllCourses
                .orderBy("search_key")
                .startAt(
                    text
                ).endAt(
                    text + '\uf8ff',
                ).limit(10).get().addOnCompleteListener {
                    newArray.clear()

                    newArray =
                        it.result.toObjects<Teachers.Courses>() as ArrayList<Teachers.Courses>
                    searchAdapter.setBooksList(newArray)

                    Log.d(mTAG, "searchAboutBook: $newArray")
                }
        } else {
            getAllCourses()
        }

    }

    private fun getAllCourses() {
        collectionRefAllCourses
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.d(mTAG, "getAllCourses: Exception => ${it.message}")
                    return@addSnapshotListener
                }
                value?.let {
                    newArray.clear()
                    Log.d(mTAG, "getAllCourses: ${it.toObjects<Teachers.Courses>()}")
                    for (document in it) {
                        val book = document.toObject<Teachers.Courses>()
                        newArray.add(book)
                    }

                    if (newArray.isEmpty()) {
                        binding.imgEmpty.visibility = View.VISIBLE
                    } else if (newArray.isNotEmpty()) {
                        binding.imgEmpty.visibility = View.GONE
                    }
                    searchAdapter.setBooksList(newArray)
                }
            }
    }


}