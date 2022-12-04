package com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.all_chapters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentShowChapterBinding
import com.nerds.m_learning.student.ui.fragments.home.PdfShowViewModel
import com.nerds.m_learning.student.ui.fragments.home.getChapters.ChapterStudentSideViewModel
import com.nerds.m_learning.teacher.model.Teachers

class ShowChapterFragment : Fragment() {
    private  val viewModel: PdfShowViewModel by activityViewModels()
    private  val chapterViewModel: ChapterStudentSideViewModel by activityViewModels()
    private lateinit var chapter: Teachers.ChapterOfLecture
    private val mTAG = "_ChapterShowFragment"

    private lateinit var binding:FragmentShowChapterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_show_chapter, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        val arg = this.arguments
        chapter = arg?.getParcelable("chapter")!!

        chapterViewModel.getChapterOfLecture(
            chapter.teacherID,
            chapter.courseID,
            chapter.lectureID,
            chapter.chapterID
        )
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

}