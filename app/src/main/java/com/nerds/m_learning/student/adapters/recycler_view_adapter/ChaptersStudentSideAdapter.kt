package com.nerds.m_learning.student.adapters.recycler_view_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellAssignmentsListBinding
import com.nerds.m_learning.databinding.CellChaptersListBinding
import com.nerds.m_learning.databinding.CellChaptersStudentSideBinding
import com.nerds.m_learning.teacher.model.Teachers

class ChaptersStudentSideAdapter(private val onClick: OnClick) :
    ListAdapter<Teachers.ChapterOfLecture, ChaptersStudentSideAdapter.ItemViewHolder>(
        LecturesDiffUtil()
    ) {


    class ItemViewHolder(val binding: CellChaptersStudentSideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chapter: Teachers.ChapterOfLecture) {
            binding.chapter = chapter

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellChaptersStudentSideBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(chapter = currentList[position])

        val chapter = currentList[position]
        holder.binding.btnShow.setOnClickListener {
            onClick.onClick(chapter)
        }
    }

    interface OnClick {
        fun onClick(chapter: Teachers.ChapterOfLecture)
    }

    class LecturesDiffUtil : DiffUtil.ItemCallback<Teachers.ChapterOfLecture>() {
        override fun areItemsTheSame(
            oldItem: Teachers.ChapterOfLecture,
            newItem: Teachers.ChapterOfLecture
        ): Boolean {
            return oldItem.chapterID == newItem.chapterID
        }

        override fun areContentsTheSame(
            oldItem: Teachers.ChapterOfLecture,
            newItem: Teachers.ChapterOfLecture
        ): Boolean {
            return oldItem == newItem
        }


    }
}