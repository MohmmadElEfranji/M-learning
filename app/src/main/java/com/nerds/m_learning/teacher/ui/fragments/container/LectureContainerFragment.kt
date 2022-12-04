package com.nerds.m_learning.teacher.ui.fragments.container

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentLectureContainerBinding
import com.nerds.m_learning.teacher.adapters.tab_layout.LectureTabLayoutAdapter

class LectureContainerFragment : Fragment() {

    private lateinit var adapter: LectureTabLayoutAdapter

    /*----------------------------------*/
    private lateinit var binding: FragmentLectureContainerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_lecture_container, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayout()


      /*  val arg = this.arguments
          val id = arg?.getInt("FragmentID")

        if (id != null) {

            binding.vpMyViewPager2.currentItem = id

        }*/

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
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.courseContainerFragment, true).build()
                findNavController().navigate(
                    R.id.action_lectureContainerFragment_to_courseContainerFragment,
                    null,
                    navOptions
                )

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


}