package com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.all_chapters

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
import com.nerds.m_learning.databinding.FragmentChaptersBinding
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.ChaptersAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner


class ChaptersFragment : Fragment(), ChaptersAdapter.OnClick {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: ChaptersViewModel

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val chaptersAdapter: ChaptersAdapter by lazy {
        ChaptersAdapter(this)
    }

    /*----------------------------------*/
    private val mTAG = "_ChaptersFragment"
    private lateinit var binding: FragmentChaptersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chapters, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/
        viewModel = ViewModelProvider(this)[ChaptersViewModel::class.java]


        binding.swipeContainer.setOnRefreshListener {

            sharedViewModel.sharedID.observe(viewLifecycleOwner) { task ->
                viewModel.getAllChapterOfLecture(teacherID, task.courseID, task.lectureID)
            }
            binding.swipeContainer.isRefreshing = false
        }
        binding.swipeContainer.setColorSchemeResources(R.color.new_color2)
        /*----------------------------------*/
        viewModel.allChapters.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {

                chaptersAdapter.submitList(task.data)

                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "ChaptersFragment: allChapters => ${task.message}")
            }
        }

        viewModel.deleteChapter.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The chapter has been successfully Deleted :)", Banner.TOP, 2000
                ).show()
                sharedViewModel.sharedID.observe(viewLifecycleOwner) { task2 ->

                    viewModel.getAllChapterOfLecture(teacherID, task2.courseID, task2.lectureID)

                }

            } else {
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "${task.message} :(", Banner.TOP, 2000
                ).show()
            }
        }
        sharedViewModel.sharedID.observe(viewLifecycleOwner) { task ->
            viewModel.getAllChapterOfLecture(teacherID, task.courseID, task.lectureID)
        }
        setUpRecycler()
        binding.fabAddChapter.setOnClickListener {
            findNavController().navigate(R.id.action_lectureContainerFragment_to_addChapterFragment)
        }
    }

    private fun setUpRecycler() {
        binding.rvChapters.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvChapters.adapter = chaptersAdapter
    }

    override fun onClick(chapter: Teachers.ChapterOfLecture, menu: View) {
        val popupMenu = PopupMenu(requireContext(), menu)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        if (chapter.visibility == false) {
            popupMenu.menu.getItem(1).title = "UnHide"
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {

                    val bundle = Bundle()
                    bundle.putParcelable("chapter", chapter)

                    findNavController().navigate(
                        R.id.action_lectureContainerFragment_to_editChapterFragment,
                        bundle
                    )
                }
                R.id.action_hide -> {
                    if (chapter.visibility == false) {
                        viewModel.updateChapterVisibilityState(chapter, true)
                    } else if (chapter.visibility == true) {
                        viewModel.updateChapterVisibilityState(chapter, false)
                    }

                }
                R.id.action_delete -> {
                    sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task ->
                        viewModel.deleteChapter(
                            teacherID,
                            task.courseID,
                            chapter.lectureID,
                            chapter.chapterID,
                            chapter.chapterFileName
                        )
                    }

                }


            }
            true
        }


        popupMenu.show()
    }

    override fun onClickShow(chapter: Teachers.ChapterOfLecture) {

        val bundle = Bundle()
        bundle.putParcelable("chapter", chapter)
        findNavController().navigate(
            R.id.action_lectureContainerFragment_to_showChapterFragment,
            bundle
        )
    }

}