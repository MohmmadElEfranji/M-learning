package com.nerds.m_learning.student.adapters.recycler_view_adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.databinding.CellRegisteredCoursesListBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.model.Teachers

class RegisteredCoursesAdapter(
    var context: Context,
    private val onCourseReceived: OnCourseReceived
) :
    ListAdapter<Students.Courses, RegisteredCoursesAdapter.ItemViewHolder>(BooksDiffUtil()) {

    private val storage = Firebase.storage
    var storageRef = storage.reference

    /*----------------------------------*/

    class ItemViewHolder(val binding: CellRegisteredCoursesListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(courses: Students.Courses) {
            binding.courses = courses
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CellRegisteredCoursesListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(courses = currentList[position])

        val course = currentList[position]
        Glide.with(holder.binding.root.context)
            .load(course.courseImage.toString())
            .into(holder.binding.imgCourseCover)


        holder.binding.btnDetails.setOnClickListener {
            onCourseReceived.onCourseReceived(course)
        }
        holder.binding.menu.setOnClickListener {
            onCourseReceived.onClick(course,holder.binding.menu)
        }
        holder.binding.btnChat.setOnClickListener {
            onCourseReceived.onChatClick(course)
        }

        onCourseReceived.onOpen(course, holder, position)


       // holder.binding.tvCourseName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        //holder.binding.tvCourseName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }

    interface OnCourseReceived {
        fun onCourseReceived(passCourseID: Students.Courses)
        fun onClick(course: Students.Courses, menu: View)
        fun onChatClick(course: Students.Courses)
        fun onOpen(course: Students.Courses,holder: ItemViewHolder,position: Int)

    }


    class BooksDiffUtil : DiffUtil.ItemCallback<Students.Courses>() {
        override fun areItemsTheSame(
            oldItem: Students.Courses,
            newItem: Students.Courses
        ): Boolean {
            return oldItem.courseID == newItem.courseID
        }

        override fun areContentsTheSame(
            oldItem: Students.Courses,
            newItem: Students.Courses
        ): Boolean {
            return oldItem == newItem
        }
    }
}

