package com.nerds.m_learning.student.ui.fragments.home.getChapters

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
import com.nerds.m_learning.databinding.FragmentChapterStudentSideBinding
import com.nerds.m_learning.student.adapters.recycler_view_adapter.ChaptersStudentSideAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel


class ChapterStudentSideFragment : Fragment(), ChaptersStudentSideAdapter.OnClick {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: ChapterStudentSideViewModel

    private val chapterAdapter: ChaptersStudentSideAdapter by lazy {
        ChaptersStudentSideAdapter(this)
    }
    private val mTAG = "_ChapterStudentSide"
    private lateinit var binding: FragmentChapterStudentSideBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_chapter_student_side,
                container,
                false
            )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChapterStudentSideViewModel::class.java]


        sharedViewModel.sharedLectureID.observe(viewLifecycleOwner) { task ->
            viewModel.getAllChapterOfLecture(task.teacherID, task.courseID, task.lectureID)

        }

        viewModel.allChapters.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {
                chapterAdapter.submitList(task.data)

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
        binding.rvChapters.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvChapters.adapter = chapterAdapter
    }

    override fun onClick(chapter: Teachers.ChapterOfLecture) {
        sharedViewModel.sharedChapterID(chapter)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ChapterShowFragment()).commit()
    }
}