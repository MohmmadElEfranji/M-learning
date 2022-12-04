package com.nerds.m_learning.teacher.adapters.tab_layout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nerds.m_learning.teacher.ui.fragments.students_of_course.CourseStudentsFragment
import com.nerds.m_learning.teacher.ui.fragments.lectures.get_lectures.LecturesFragment

class CourseTabLayoutAdapter(fragmentActivity: FragmentActivity) :
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
                fragment = LecturesFragment()
            }
            1 -> {
                fragment = CourseStudentsFragment()
            }
        }

        return fragment
    }
}