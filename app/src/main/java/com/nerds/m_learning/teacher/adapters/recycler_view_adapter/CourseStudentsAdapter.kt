package com.nerds.m_learning.teacher.adapters.recycler_view_adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellStudentsListBinding
import com.nerds.m_learning.teacher.model.Teachers

class CourseStudentsAdapter(private val onChatClick: OnChatClick) :
    ListAdapter<Teachers.CourseStudents, CourseStudentsAdapter.ItemViewHolder>(LecturesDiffUtil()) {


    class ItemViewHolder(val binding: CellStudentsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(courseStudents: Teachers.CourseStudents) {
            binding.courseStudents = courseStudents

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellStudentsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(courseStudents = currentList[position])

        val student = currentList[position]
        holder.binding.tvStudentEmail.setOnClickListener {
            onChatClick.onClick(student)
        }
    }


    interface OnChatClick {
        fun onClick(studentID: Teachers.CourseStudents)
    }

    class LecturesDiffUtil : DiffUtil.ItemCallback<Teachers.CourseStudents>() {
        override fun areItemsTheSame(
            oldItem: Teachers.CourseStudents,
            newItem: Teachers.CourseStudents
        ): Boolean {
            return oldItem.StudentID == newItem.StudentID
        }

        override fun areContentsTheSame(
            oldItem: Teachers.CourseStudents,
            newItem: Teachers.CourseStudents
        ): Boolean {
            return oldItem == newItem
        }


    }


}