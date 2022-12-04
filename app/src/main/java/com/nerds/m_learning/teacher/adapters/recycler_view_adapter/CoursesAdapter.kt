package com.nerds.m_learning.teacher.adapters.recycler_view_adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.databinding.CellTeacherCoursesListBinding
import com.nerds.m_learning.teacher.model.Teachers

class CoursesAdapter(
    var context: Context, private val onCourseReceived: OnCourseReceived,
    private val onClick: OnClick
) :
    ListAdapter<Teachers.Courses, CoursesAdapter.ItemViewHolder>(BooksDiffUtil()) {

    private val storage = Firebase.storage
    var storageRef = storage.reference

    /*----------------------------------*/

    class ItemViewHolder(val binding: CellTeacherCoursesListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Teachers.Courses) {
            binding.course = course
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CellTeacherCoursesListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(course = currentList[position])

        val course = currentList[position]
        Glide.with(holder.binding.root.context)
            .load(course.courseImage.toString())
            .into(holder.binding.imgCourseCover)
        val courseID = Teachers.PassCourseID(course.courseID)

        holder.binding.btnDetails.setOnClickListener {
            onCourseReceived.onCourseReceived(courseID)
        }

        holder.binding.menu.setOnClickListener {
            onClick.onClick(course, holder.binding.menu)
        }

    }

    interface OnCourseReceived {
        fun onCourseReceived(passCourseID: Teachers.PassCourseID)
    }

    interface OnClick {
        fun onClick(course: Teachers.Courses, menu: View)
    }

    class BooksDiffUtil : DiffUtil.ItemCallback<Teachers.Courses>() {
        override fun areItemsTheSame(
            oldItem: Teachers.Courses,
            newItem: Teachers.Courses
        ): Boolean {
            return oldItem.courseID == newItem.courseID
        }

        override fun areContentsTheSame(
            oldItem: Teachers.Courses,
            newItem: Teachers.Courses
        ): Boolean {
            return oldItem == newItem
        }


    }
}

