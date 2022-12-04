package com.nerds.m_learning.student.adapters.recycler_view_adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nerds.m_learning.databinding.CellInterestsListBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.teacher.model.Teachers

class InterestsAdapter(var context: Context, private val onClickCourse: OnClickCourse,private val onOpen: OnClickCourse) :
    ListAdapter<Teachers.Courses, InterestsAdapter.ItemViewHolder>(BooksDiffUtil()) {
    var dataOfCourses: MutableList<Teachers.Courses> = ArrayList()

    /*----------------------------------*/
    private var itemClick: OnItemClick? = null
    private var selectedItems: SparseBooleanArray? = null
    private var selectedIndex = -1
    /*----------------------------------*/

    init {
        selectedItems = SparseBooleanArray()

    }
    /*----------------------------------*/

    class ItemViewHolder(val binding: CellInterestsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Teachers.Courses) {
            binding.allCourses = course
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CellInterestsListBinding.inflate(
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

        holder.binding.btnSubscribe.setOnClickListener {
            onClickCourse.onClickCourse(course)
        }

        /* val sh = mutableListOf<String>("6VQZk5SCSVys033ULCEL","GE7PNbosX5YmbvDMii3J")
         //currentList.contains(sh)
         for (i in sh){
             if (course.courseID == i){
                 holder.binding.btnSubscribe.text ="NO"
             }

         }*/


      //  holder.binding.btnSubscribe.text ="no"

        onOpen.onOpen(course,position,holder)
    }

    interface OnItemClick {
        fun onItemClick(view: View?, allCourses: Teachers.Courses, position: Int)
    }

    fun setItemClick(itemClick: OnItemClick?) {
        this.itemClick = itemClick
    }

    interface OnClickCourse {
        fun onClickCourse(passCourse: Teachers.Courses)
        fun onOpen(passCourse: Teachers.Courses, position: Int, holder: ItemViewHolder)
    }

    //endregion

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

