package com.nerds.m_learning.student.adapters.tab_layout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nerds.m_learning.student.ui.fragments.home.getAssignments.AssignmentStudentSideFragment
import com.nerds.m_learning.student.ui.fragments.home.getChapters.ChapterStudentSideFragment

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
                 fragment = AssignmentStudentSideFragment()
            }
            1 -> {
                fragment = ChapterStudentSideFragment()
            }
        }

        return fragment
    }
}