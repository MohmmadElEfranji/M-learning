package com.nerds.m_learning.teacher.adapters.recycler_view_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerds.m_learning.databinding.CellAssignmentsListBinding
import com.nerds.m_learning.databinding.CellChaptersListBinding
import com.nerds.m_learning.teacher.model.Teachers

class ChaptersAdapter(private val onClick: OnClick) :
    ListAdapter<Teachers.ChapterOfLecture, ChaptersAdapter.ItemViewHolder>(
        LecturesDiffUtil()
    ) {


    class ItemViewHolder(val binding: CellChaptersListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chapter: Teachers.ChapterOfLecture) {
            binding.chapter = chapter

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CellChaptersListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(chapter = currentList[position])

        val chapter = currentList[position]
        holder.binding.menu.setOnClickListener {
            onClick.onClick(chapter, holder.binding.menu)
        }

        holder.binding.btnDetails.setOnClickListener {
            onClick.onClickShow(chapter)
        }


    }

    interface OnClick {
        fun onClick(chapter: Teachers.ChapterOfLecture, menu: View)
        fun onClickShow(chapter: Teachers.ChapterOfLecture)
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