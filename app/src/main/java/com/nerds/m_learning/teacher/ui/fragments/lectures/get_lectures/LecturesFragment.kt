package com.nerds.m_learning.teacher.ui.fragments.lectures.get_lectures

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentLecturesBinding
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.LecturesAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.nerds.m_learning.teacher.ui.fragments.students_of_course.CourseStudentsViewModel


class LecturesFragment : Fragment(), LecturesAdapter.OnLectureReceived, LecturesAdapter.OnClick {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: LecturesViewModel

    /*----------------------------------*/
    private val lecturesAdapter: LecturesAdapter by lazy {
        LecturesAdapter(this, this)
    }

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    private val courseStudentsViewModel: CourseStudentsViewModel by activityViewModels()

    /*----------------------------------*/
    private val mTAG = "_LecturesFragment"
    private lateinit var binding: FragmentLecturesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_lectures, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[LecturesViewModel::class.java]


        /*----------------------------------*/
        binding.swipeContainer.setOnRefreshListener {

            sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task ->

                viewModel.getAllLecturesOfCourse(teacherID, task.courseID)

            }
            binding.swipeContainer.isRefreshing = false
        }
        binding.swipeContainer.setColorSchemeResources(R.color.new_color2)

        /*----------------------------------*/

        viewModel.allLectures.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {
                lecturesAdapter.submitList(task.data)

                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "StudentHomeFragment: getAllCourses2 => ${task.message}")
            }
        }


        viewModel.deleteLecture.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                Toast.makeText(
                    requireActivity(),
                    "The course has been successfully Deleted :)",
                    Toast.LENGTH_SHORT
                ).show()

                sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task2 ->
                    viewModel.getAllLecturesOfCourse(teacherID, task2.courseID)
                }

            } else {
                Toast.makeText(
                    requireActivity(),
                    "${task.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        /*----------------------------------*/
        sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task ->

            viewModel.getAllLecturesOfCourse(teacherID, task.courseID)

        }

        setUpRecycler()
        /*----------------------------------*/
        binding.fabAddLecture.setOnClickListener {
            findNavController().navigate(R.id.action_courseContainerFragment_to_addLectureFragment)
        }

    }

    override fun onResume() {
        super.onResume()


    }

    private fun setUpRecycler() {
        binding.rvLectures.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvLectures.adapter = lecturesAdapter
    }

    override fun onLectureReceived(passID: Teachers.PassID) {
        sharedViewModel.publishCourseID(passID, passID)
        findNavController().navigate(R.id.action_courseContainerFragment_to_lectureContainerFragment)
    }

    override fun onClick(lecture: Teachers.LecturesOfCourse, menu: View) {
        val popupMenu = PopupMenu(requireContext(), menu)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        if (lecture.visibility == false) {
            popupMenu.menu.getItem(1).title = "UnHide"
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {

                    val bundle = Bundle()
                    bundle.putParcelable("lecture", lecture)

                    findNavController().navigate(
                        R.id.action_courseContainerFragment_to_editLectureFragment,
                        bundle
                    )
                }
                R.id.action_hide -> {
                    updateLectureVisibilityState(lecture)

                }
                R.id.action_delete -> {
                    viewModel.deleteLecture(
                        teacherID,
                        lecture.courseID,
                        lecture.lectureID,
                        lecture.lectureVideoName
                    )
                }


            }
            true
        }


        popupMenu.show()
    }

    private fun updateLectureVisibilityState(lecture: Teachers.LecturesOfCourse) {
        courseStudentsViewModel.getAllStudentsOfCourse(teacherID, lecture.courseID)
        if (lecture.visibility == false) {
            courseStudentsViewModel.allStudentsOfCourse.observe(viewLifecycleOwner) {
                for (i in it.data!!) {
                    viewModel.updateLectureVisibilityStateFromStudents(
                        i.StudentID,
                        lecture, true
                    )
                }
            }
            viewModel.updateLectureVisibilityState(lecture, true)


        } else if (lecture.visibility == true) {
            courseStudentsViewModel.allStudentsOfCourse.observe(viewLifecycleOwner) {
                for (i in it.data!!) {
                    viewModel.updateLectureVisibilityStateFromStudents(
                        i.StudentID,
                        lecture, false
                    )
                }
            }
            viewModel.updateLectureVisibilityState(lecture, false)
        }
    }

}