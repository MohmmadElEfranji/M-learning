package com.nerds.m_learning.student.adapters.recycler_view_adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellLecturesStudentSideListBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.model.Teachers

class LecturesStudentSideAdapter(private val onLectureReceived: OnLectureReceived) :
    ListAdapter<Students.LecturesOfCourse, LecturesStudentSideAdapter.ItemViewHolder>(LecturesDiffUtil()) {


    class ItemViewHolder(val binding: CellLecturesStudentSideListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lecture: Students.LecturesOfCourse) {
            binding.lecture = lecture

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellLecturesStudentSideListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(lecture = currentList[position])
        val lecture = currentList[position]

        holder.binding.btnDetails.setOnClickListener {
            onLectureReceived.onLectureReceived(lecture)
        }
        holder.binding.btnShow.setOnClickListener {
            onLectureReceived.passDataOfLecture(lecture,position)
        }

        onLectureReceived.onOpen(lecture,holder,position)
    }

    interface OnLectureReceived {
        fun onLectureReceived(passID: Students.LecturesOfCourse)
        fun passDataOfLecture(passID: Students.LecturesOfCourse,position: Int)
        fun onOpen(passID: Students.LecturesOfCourse,holder: ItemViewHolder,position: Int)
    }


    class LecturesDiffUtil : DiffUtil.ItemCallback<Students.LecturesOfCourse>() {
        override fun areItemsTheSame(
            oldItem: Students.LecturesOfCourse,
            newItem: Students.LecturesOfCourse
        ): Boolean {
            return oldItem.lectureID == newItem.lectureID
        }

        override fun areContentsTheSame(
            oldItem: Students.LecturesOfCourse,
            newItem: Students.LecturesOfCourse
        ): Boolean {
            return oldItem == newItem
        }


    }


}