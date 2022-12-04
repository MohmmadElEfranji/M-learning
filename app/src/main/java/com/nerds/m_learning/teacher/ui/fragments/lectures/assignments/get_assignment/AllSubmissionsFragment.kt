package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.get_assignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentAllSubmissionsBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.SubmissionAdapter
import com.nerds.m_learning.teacher.model.Teachers

class AllSubmissionsFragment : Fragment(), SubmissionAdapter.OnClick {

    private val submissionAdapter: SubmissionAdapter by lazy {
        SubmissionAdapter(this)
    }
    private val viewModel: AssignmentViewModel by activityViewModels()
    private lateinit var assignment: Teachers.AssignmentsOfLecture

    private val mTAG = "_AllSubmissionsFragment"
    private lateinit var binding: FragmentAllSubmissionsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_all_submissions, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecycler()
        binding.progressBar.visibility = View.VISIBLE
        val arg = this.arguments
        assignment = arg?.getParcelable("assignment")!!
        viewModel.getAllSubmissionOfAssignment(
            assignment.teacherID,
            assignment.courseID,
            assignment.lectureID,
            assignment.assignmentID
        )

        viewModel.allSubmissionOfAssignment.observe(viewLifecycleOwner) {
            submissionAdapter.submitList(it.data)
            binding.progressBar.visibility = View.GONE
            Log.d(mTAG, "onViewCreated: ${it.data}")
        }
    }

    override fun onClick(assignment: Students.SubmissionAssignmentsOfLecture) {
        val bundle = Bundle()
        bundle.putString("assignment", assignment.assignmentFile)
        findNavController().navigate(
            R.id.action_allSubmissionsFragment_to_showAssignmentSubmissionFragment2,
            bundle
        )
    }

    private fun setUpRecycler() {
        binding.rvSubmissions.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvSubmissions.adapter = submissionAdapter
    }
}