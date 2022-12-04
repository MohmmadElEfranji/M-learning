package com.nerds.m_learning.student.ui.fragments.container

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentContainerBinding
import com.nerds.m_learning.student.adapters.tab_layout.LectureTabLayoutAdapter
import com.nerds.m_learning.student.ui.fragments.home.getLectures.LecturesStudentSideFragment
import com.nerds.m_learning.teacher.ui.SharedViewModel


class ContainerFragment : Fragment() {
    private lateinit var adapter: LectureTabLayoutAdapter

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentContainerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_container, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar?.hide()

        setupTabLayout()

    }

    private fun setupTabLayout() {
        adapter = LectureTabLayoutAdapter(requireActivity())
        binding.vpMyViewPager2.adapter = adapter

        val tabLayoutMediator = TabLayoutMediator(
            binding.tlMyTabLayout, binding.vpMyViewPager2
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Assignments"

                    tab.setIcon(R.drawable.ic_assignment)


                }
                1 -> {
                    tab.text = "Chapters"
                    tab.setIcon(R.drawable.ic_slide)

                }
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, LecturesStudentSideFragment()).commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}