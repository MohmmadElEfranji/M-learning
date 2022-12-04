package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.get_assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentShowAssignmentSubmissionBinding
import com.nerds.m_learning.student.ui.fragments.home.PdfShowViewModel


class ShowAssignmentSubmissionFragment : Fragment() {

    private val pdfShowViewModel: PdfShowViewModel by activityViewModels()
    private lateinit var binding: FragmentShowAssignmentSubmissionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_show_assignment_submission,
                container,
                false
            )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE

        val arg = this.arguments
        val assignmentFile = arg?.getString("assignment")!!
        pdfShowViewModel.getInputStreamFromURL(assignmentFile)

        pdfShowViewModel.url.observe(viewLifecycleOwner) {
            binding.pdfView.fromStream(it).load()
            binding.progressBar.visibility = View.GONE
        }

    }
}