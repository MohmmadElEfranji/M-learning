package com.nerds.m_learning.teacher.adapters.recycler_view_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellAssignmentsListBinding
import com.nerds.m_learning.teacher.model.Teachers

class AssignmentAdapter(private val onClick: OnClick) :
    ListAdapter<Teachers.AssignmentsOfLecture, AssignmentAdapter.ItemViewHolder>(
        LecturesDiffUtil()
    ) {


    class ItemViewHolder(val binding: CellAssignmentsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Teachers.AssignmentsOfLecture) {
            binding.assignment = assignment

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellAssignmentsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(assignment = currentList[position])

        val assignment = currentList[position]
        holder.binding.menu.setOnClickListener {
            onClick.onClick(assignment, holder.binding.menu)
        }

        holder.binding.btnShowSubmission.setOnClickListener {
            onClick.onClickSubmissions(assignment)
        }

        holder.binding.btnShow.setOnClickListener {
            onClick.onClickShow(assignment)
        }
    }

    interface OnClick {
        fun onClick(assignment: Teachers.AssignmentsOfLecture, menu: View)
        fun onClickSubmissions(assignment: Teachers.AssignmentsOfLecture)
        fun onClickShow(assignment: Teachers.AssignmentsOfLecture)
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