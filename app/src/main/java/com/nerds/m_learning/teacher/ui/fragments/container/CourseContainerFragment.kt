package com.nerds.m_learning.teacher.ui.fragments.container

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentCourseContainerBinding
import com.nerds.m_learning.teacher.adapters.tab_layout.CourseTabLayoutAdapter


class CourseContainerFragment : Fragment() {

    private lateinit var adapter: CourseTabLayoutAdapter

    /*----------------------------------*/
    private lateinit var binding: FragmentCourseContainerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_course_container, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayout()

    }

    private fun setupTabLayout() {
        adapter = CourseTabLayoutAdapter(requireActivity())
        binding.vpMyViewPager2.adapter = adapter

        val tabLayoutMediator = TabLayoutMediator(
            binding.tlMyTabLayout, binding.vpMyViewPager2
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Lectures"

                    tab.setIcon(R.drawable.ic_lectures)


                }
                1 -> {
                    tab.text = "Students"
                    tab.setIcon(R.drawable.ic_students)

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
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                findNavController().navigate(
                    R.id.action_courseContainerFragment_to_homeFragment,
                    null,
                    navOptions
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


}