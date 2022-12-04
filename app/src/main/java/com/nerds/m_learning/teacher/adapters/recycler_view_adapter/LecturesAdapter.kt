package com.nerds.m_learning.teacher.adapters.recycler_view_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellLecturesListBinding
import com.nerds.m_learning.teacher.model.Teachers

class LecturesAdapter(private val onLectureReceived: OnLectureReceived, private val onClick: OnClick) :
    ListAdapter<Teachers.LecturesOfCourse, LecturesAdapter.ItemViewHolder>(LecturesDiffUtil()) {


    class ItemViewHolder(val binding: CellLecturesListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lecture: Teachers.LecturesOfCourse) {
            binding.lecture = lecture

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellLecturesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(lecture = currentList[position])
        val lecture = currentList[position]
        val sharedID = Teachers.PassID(lecture.lectureID,lecture.courseID)

        holder.binding.btnDetails.setOnClickListener {
            onLectureReceived.onLectureReceived(sharedID)
        }
        holder.binding.menu.setOnClickListener {
          onClick.onClick(lecture,holder.binding.menu)
        }
    }

    interface OnLectureReceived {
        fun onLectureReceived(passID: Teachers.PassID)
    }
    interface OnClick {
        fun onClick(lecture: Teachers.LecturesOfCourse,menu: View)
    }

    class LecturesDiffUtil : DiffUtil.ItemCallback<Teachers.LecturesOfCourse>() {
        override fun areItemsTheSame(
            oldItem: Teachers.LecturesOfCourse,
            newItem: Teachers.LecturesOfCourse
        ): Boolean {
            return oldItem.lectureID == newItem.lectureID
        }

        override fun areContentsTheSame(
            oldItem: Teachers.LecturesOfCourse,
            newItem: Teachers.LecturesOfCourse
        ): Boolean {
            return oldItem == newItem
        }


    }


}