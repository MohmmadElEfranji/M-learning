package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.get_assignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentShowAssignmentBinding
import com.nerds.m_learning.student.ui.fragments.home.PdfShowViewModel
import com.nerds.m_learning.student.ui.fragments.home.getAssignments.AssignmentStudentSideViewModel
import com.nerds.m_learning.teacher.model.Teachers


class ShowAssignmentFragment : Fragment() {

    private val viewModel: AssignmentViewModel by activityViewModels()
    private lateinit var assignment: Teachers.AssignmentsOfLecture
    private val pdfShowViewModel: PdfShowViewModel by activityViewModels()
    private val assignmentViewModel: AssignmentStudentSideViewModel by activityViewModels()

    private val mTAG = "_ShowAssignment"
    private lateinit var binding: FragmentShowAssignmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_show_assignment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        val arg = this.arguments
        assignment = arg?.getParcelable("assignment")!!
        assignmentViewModel.getAssignmentsLecture(
            assignment.teacherID,
            assignment.courseID,
            assignment.lectureID,
            assignment.assignmentID
        )
        pdfShowViewModel.url.observe(viewLifecycleOwner) {
            binding.pdfView.fromStream(it).load()
            binding.progressBar.visibility = View.GONE
        }

        assignmentViewModel.assignment.observe(viewLifecycleOwner) { task ->
            task.data?.let {
                pdfShowViewModel.getInputStreamFromURL(task.data.assignmentFile)
            }
            Log.d(mTAG, "StudentHomeFragment: getAllCourses2 => ${task.message}")

        }
    }

}