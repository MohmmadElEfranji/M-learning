package com.nerds.m_learning.teacher.adapters.tab_layout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.get_assignment.AssignmentFragment
import com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.all_chapters.ChaptersFragment


class LectureTabLayoutAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val TOTAL_COUNT = 2
    }

    override fun getItemCount(): Int {
        return TOTAL_COUNT
    }


    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()

        when (position) {
            0 -> {
                fragment = AssignmentFragment()
            }
            1 -> {
                fragment = ChaptersFragment()
            }
        }

        return fragment
    }
}