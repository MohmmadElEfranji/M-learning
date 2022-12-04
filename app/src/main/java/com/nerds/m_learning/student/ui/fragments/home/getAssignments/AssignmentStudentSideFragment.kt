package com.nerds.m_learning.student.ui.fragments.home.getAssignments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentAssignmentStudentSideBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.AssignmentStudentSideAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel


class AssignmentStudentSideFragment : Fragment(), AssignmentStudentSideAdapter.OnClick {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: AssignmentStudentSideViewModel

    /*----------------------------------*/

    private val assignmentAdapter: AssignmentStudentSideAdapter by lazy {
        AssignmentStudentSideAdapter(this)
    }

    /*----------------------------------*/
    private val mTAG = "_AssignmentStudentSide"
    private lateinit var binding: FragmentAssignmentStudentSideBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_assignment_student_side,
                container,
                false
            )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AssignmentStudentSideViewModel::class.java]


        sharedViewModel.sharedLectureID.observe(viewLifecycleOwner) { task ->

            viewModel.getAllAssignmentsOfLecture(task.teacherID, task.courseID, task.lectureID)

        }

        viewModel.allAssignments.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {
                assignmentAdapter.submitList(task.data)
                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "AssignmentFragment: allAssignments => ${task.message}")
            }
        }
        setUpRecycler()
    }

    private fun setUpRecycler() {
        binding.rvAssignments.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvAssignments.adapter = assignmentAdapter
    }

    override fun onClick(assignment: Teachers.AssignmentsOfLecture) {
        sharedViewModel.sharedAssignmentID(assignment)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, AssignmentShowFragment()).commit()
    }

    override fun onClickSubmissions(assignment: Teachers.AssignmentsOfLecture) {
        sharedViewModel.sharedAssignmentID(assignment)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, SubmissionAssignmentFragment()).commit()
    }
}