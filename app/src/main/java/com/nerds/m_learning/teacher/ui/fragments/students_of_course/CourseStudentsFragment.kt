package com.nerds.m_learning.teacher.ui.fragments.students_of_course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.nerds.m_learning.databinding.FragmentCourseStudentsBinding
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.CourseStudentsAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel


class CourseStudentsFragment : Fragment(), CourseStudentsAdapter.OnChatClick {
    private lateinit var viewModel: CourseStudentsViewModel

    /*----------------------------------*/
    private val courseStudentsAdapter: CourseStudentsAdapter by lazy {
        CourseStudentsAdapter(this)
    }

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    private val mTAG = "_CourseStudentsFragment"
    private lateinit var binding: FragmentCourseStudentsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_course_students, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CourseStudentsViewModel::class.java]

        viewModel.allStudentsOfCourse.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {

                courseStudentsAdapter.submitList(task.data)

                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "CourseStudentsFragment: getAllCourses2 => ${task.message}")
            }
        }

        sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task2 ->
            viewModel.getAllStudentsOfCourse(teacherID, task2.courseID)
        }
        setUpRecycler()


    }

    private fun setUpRecycler() {
        binding.rvCourseStudents.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCourseStudents.adapter = courseStudentsAdapter
    }

    override fun onClick(studentID: Teachers.CourseStudents) {
        val bundle = Bundle()

        bundle.putParcelable("student", studentID)
        findNavController().navigate(
            R.id.action_courseContainerFragment_to_chatPrivateFragment,
            bundle
        )
    }

}