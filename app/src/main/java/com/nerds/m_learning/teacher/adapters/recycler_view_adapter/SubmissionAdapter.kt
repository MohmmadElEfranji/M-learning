package com.nerds.m_learning.teacher.adapters.recycler_view_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellSubmissionsListBinding
import com.nerds.m_learning.student.model.Students

class SubmissionAdapter(private val onClick: OnClick) :
    ListAdapter<Students.SubmissionAssignmentsOfLecture, SubmissionAdapter.ItemViewHolder>(
        LecturesDiffUtil()
    ) {


    class ItemViewHolder(val binding: CellSubmissionsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Students.SubmissionAssignmentsOfLecture) {
            binding.submission = assignment

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellSubmissionsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(assignment = currentList[position])

        val assignment = currentList[position]

        holder.binding.btnDetails.setOnClickListener {
            onClick.onClick(assignment)
        }

    }

    interface OnClick {
        fun onClick(assignment: Students.SubmissionAssignmentsOfLecture)
    }

    class LecturesDiffUtil : DiffUtil.ItemCallback<Students.SubmissionAssignmentsOfLecture>() {
        override fun areItemsTheSame(
            oldItem: Students.SubmissionAssignmentsOfLecture,
            newItem: Students.SubmissionAssignmentsOfLecture
        ): Boolean {
            return oldItem.studentID == newItem.studentID
        }

        override fun areContentsTheSame(
            oldItem: Students.SubmissionAssignmentsOfLecture,
            newItem: Students.SubmissionAssignmentsOfLecture
        ): Boolean {
            return oldItem == newItem
        }


    }
}