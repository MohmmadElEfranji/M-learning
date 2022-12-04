package com.nerds.m_learning.student.adapters.recycler_view_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellAssignmentsStudentSideBinding
import com.nerds.m_learning.teacher.model.Teachers

class AssignmentStudentSideAdapter(private val onClick: OnClick) :
    ListAdapter<Teachers.AssignmentsOfLecture, AssignmentStudentSideAdapter.ItemViewHolder>(
        LecturesDiffUtil()
    ) {


    class ItemViewHolder(val binding: CellAssignmentsStudentSideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Teachers.AssignmentsOfLecture) {
            binding.assignment = assignment

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellAssignmentsStudentSideBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(assignment = currentList[position])

        val assignment = currentList[position]

        holder.binding.btnShow.setOnClickListener {
            onClick.onClick(assignment)
        }

        holder.binding.btnAddSubmission.setOnClickListener {
            onClick.onClickSubmissions(assignment)
        }
        //    onOpen.onOpen(assignment)
    }

    interface OnClick {
        fun onClick(assignment: Teachers.AssignmentsOfLecture)
        fun onClickSubmissions(assignment: Teachers.AssignmentsOfLecture)
    }

    class LecturesDiffUtil : DiffUtil.ItemCallback<Teachers.AssignmentsOfLecture>() {
        override fun areItemsTheSame(
            oldItem: Teachers.AssignmentsOfLecture,
            newItem: Teachers.AssignmentsOfLecture
        ): Boolean {
            return oldItem.assignmentID == newItem.assignmentID
        }

        override fun areContentsTheSame(
            oldItem: Teachers.AssignmentsOfLecture,
            newItem: Teachers.AssignmentsOfLecture
        ): Boolean {
            return oldItem == newItem
        }


    }
}