package com.nerds.m_learning.student.ui.fragments.home.getChapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentChapterShowBinding
import com.nerds.m_learning.student.ui.fragments.container.ContainerFragment
import com.nerds.m_learning.student.ui.fragments.home.PdfShowViewModel
import com.nerds.m_learning.teacher.ui.SharedViewModel

class ChapterShowFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var viewModel: PdfShowViewModel
    private lateinit var chapterViewModel: ChapterStudentSideViewModel

    private val mTAG = "_ChapterShowFragment"
    private lateinit var binding: FragmentChapterShowBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chapter_show, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PdfShowViewModel::class.java]
        chapterViewModel = ViewModelProvider(this)[ChapterStudentSideViewModel::class.java]
        binding.progressBar.visibility = View.VISIBLE

        sharedViewModel.sharedChapterID.observe(viewLifecycleOwner) { task ->
            chapterViewModel.getChapterOfLecture(
                task.teacherID,
                task.courseID,
                task.lectureID,
                task.chapterID
            )
        }
        viewModel.url.observe(viewLifecycleOwner) {
            binding.pdfView.fromStream(it).load()
            binding.progressBar.visibility = View.GONE
        }

        chapterViewModel.chapter.observe(viewLifecycleOwner) { task ->
            task.data?.let {
                viewModel.getInputStreamFromURL(task.data.chapterFile)
            }
            Log.d(mTAG, "StudentHomeFragment: getAllCourses2 => ${task.message}")

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callBack = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ContainerFragment()).commit()


            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }
}