package com.nerds.m_learning.student.ui.fragments.home.getLectures

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentLecturesStudentSideBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.LecturesStudentSideAdapter
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.ui.fragments.container.ContainerFragment
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeFragment
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.nerds.m_learning.teacher.ui.fragments.lectures.get_lectures.LecturesViewModel
import com.shasin.notificationbanner.Banner

class LecturesStudentSideFragment : Fragment(), LecturesStudentSideAdapter.OnLectureReceived {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val lecturesViewModel: LecturesViewModel by activityViewModels()
    private lateinit var viewModel: LecturesStudentSideViewModel
    private lateinit var analytics: FirebaseAnalytics

    /*----------------------------------*/
    private val lecturesAdapter: LecturesStudentSideAdapter by lazy {
        LecturesStudentSideAdapter(this)
    }
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val mTAG = "_LecturesStudentSide"
    private lateinit var binding: FragmentLecturesStudentSideBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lectures_student_side,
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/
        viewModel = ViewModelProvider(this)[LecturesStudentSideViewModel::class.java]
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        /*----------------------------------*/

        // lecturesViewModel.getAllLecturesOfCourse(task.teacherID, task.courseID)
        sharedViewModel.sharedCourseID2.observe(viewLifecycleOwner) { task ->
            viewModel.getAllLecturesOfCourse(studentID, task.courseID)
        }

        viewModel.allLectures.observe(viewLifecycleOwner) { task ->

            val t = task.data
            if (t!!.isEmpty()) {
                binding.imgEmpty.visibility = View.VISIBLE
                sharedViewModel.sharedCourseID2.observe(viewLifecycleOwner) { task2 ->
                    lecturesViewModel.getAllLecturesOfCourse(task2.teacherID, task2.courseID)
                }
                lecturesViewModel.allLectures.observe(viewLifecycleOwner) { task3 ->
                    if (task3.data != null) {
                        val mLecture = task3.data
                        for (i in mLecture) {
                            val l = Students.LecturesOfCourse(
                                i.lectureID,
                                i.courseID,
                                i.teacherID,
                                i.lectureName,
                                i.lectureDescription,
                                i.lectureVideo,
                                i.lectureVideoName,
                            )

                            viewModel.addLecture(l, studentID)


                        }
                    } else {
                        Log.d(mTAG, "StudentHomeFragment: getAllCourses2 => ${task.message}")
                    }
                }

            } else if (t.isNotEmpty()) {
                lecturesAdapter.submitList(t)
                binding.imgEmpty.visibility = View.GONE
            }

            viewModel.addLecture.observe(viewLifecycleOwner) {
                if (it.data == true) {
                    sharedViewModel.sharedCourseID2.observe(viewLifecycleOwner) { task ->
                        viewModel.getAllLecturesOfCourse(studentID, task.courseID)
                    }
                }
            }

        }
        setUpRecycler()


    }

    private fun setUpRecycler() {
        binding.rvLectures.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvLectures.adapter = lecturesAdapter
    }

    override fun onLectureReceived(passID: Students.LecturesOfCourse) {
        sharedViewModel.publishLectureID(passID)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ContainerFragment()).commit()
    }

    override fun passDataOfLecture(passID: Students.LecturesOfCourse, position: Int) {

        when {

            position == 0 -> {
                viewModel.updateLectureWatchedState(studentID, passID)

                sharedViewModel.publishLectureID(passID)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, VideoShowFragment()).commit()

                val params = Bundle()
                params.putString("lecture", passID.lectureName)
                analytics.logEvent("lecture_viewed", params)
            }
            position > 0 -> {
                val previousItem: Students.LecturesOfCourse =
                    lecturesAdapter.currentList[position - 1]

                val watched = previousItem.watched

                if (watched == true) {
                    viewModel.updateLectureWatchedState(studentID, passID)
                    sharedViewModel.publishLectureID(passID)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, VideoShowFragment()).commit()
                    val params = Bundle()
                    params.putString("lecture", passID.lectureName)
                    analytics.logEvent("lecture_viewed", params)
                } else if (watched == false) {
                    Banner.make(
                        binding.root, requireActivity(), Banner.INFO,
                        "You must watch the previous lecture first:)", Banner.BOTTOM, 3000
                    ).show()
                }

            }
        }


    }

    override fun onOpen(
        passID: Students.LecturesOfCourse,
        holder: LecturesStudentSideAdapter.ItemViewHolder,
        position: Int
    ) {

        if (position ==0){
            holder.binding.imgLock.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_unlock,
                    null
                )
            )

        }
        if (passID.watched == true) {

            holder.binding.imgLock.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_unlock,
                    null
                )
            )
        }

        val array = lecturesAdapter.currentList
        val notComp: MutableList<Students.LecturesOfCourse> = ArrayList()
        val m = array.filter {
            it.watched == false
        }
        notComp.addAll(m)

        if (notComp.isEmpty()) {
            viewModel.updateCourseFinishedState(studentID, passID.courseID)
            Toast.makeText(
                requireContext(),
                "You have completed this course",
                Toast.LENGTH_SHORT
            )
                .show()
        } else if (notComp.isNotEmpty()) {

            notComp.clear()
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callBack = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, StudentHomeFragment()).commit()

                sharedViewModel.mSelectedFragment.value = 6
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }


}