package com.nerds.m_learning.student.adapters.recycler_view_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nerds.m_learning.databinding.CellAllCoursesSearchListBinding
import com.nerds.m_learning.teacher.model.Teachers

class SearchAdapter(var context: Context) : RecyclerView.Adapter<SearchAdapter.ItemViewHolder>() {
    var dataOfCourses: MutableList<Teachers.Courses> = ArrayList()


    /*----------------------------------*/
    private var itemClick: OnItemClick? = null
    private var selectedItems: SparseBooleanArray? = null
    private var selectedIndex = -1

    /*----------------------------------*/
    private var courseList = java.util.ArrayList<Teachers.Courses>()

    fun setBooksList(list: java.util.ArrayList<Teachers.Courses>) {
        courseList = list
        notifyDataSetChanged()
    }

    init {
        selectedItems = SparseBooleanArray()

    }
    /*----------------------------------*/

    class ItemViewHolder(val binding: CellAllCoursesSearchListBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CellAllCoursesSearchListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val courses = courseList[position]
        holder.binding.tvCourseName.text = courses.courseName
        holder.binding.tvCourseDescription.text = courses.courseDescription
        holder.binding.tvCourseField.text = courses.courseField

        holder.binding.apply {
            //Changes the activated state of this view.
            parentLayout.isActivated = selectedItems!![position, false]

            parentLayout.setOnClickListener(View.OnClickListener { view ->
                if (itemClick == null) return@OnClickListener
                itemClick!!.onItemClick(view, courseList[position], position)
            })

            parentLayout.setOnLongClickListener { view ->
                if (itemClick == null) {
                    false
                } else {
                    itemClick!!.onLongPress(view, courseList[position], position)
                    true
                }
            }
            toggleIcon(holder.binding, position)

        }


        Glide.with(holder.binding.root.context)
            .load(courses.courseImage.toString())
            .into(holder.binding.imgCourseCover)

    }

    interface OnItemClick {
        fun onLongPress(view: View?, allCourses: Teachers.Courses, position: Int)
        fun onItemClick(view: View?, allCourses: Teachers.Courses, position: Int)
    }

    fun setItemClick(itemClick: OnItemClick?) {
        this.itemClick = itemClick
    }

    // this function will toggle the selection of items
    fun toggleSelection(position: Int, allCourses: Teachers.Courses) {
        selectedIndex = position
        if (selectedItems!![position, false]) {
            selectedItems!!.delete(position)
            dataOfCourses.remove(allCourses)
        } else {
            selectedItems!!.put(position, true)
            dataOfCourses.add(allCourses)

        }
        notifyItemChanged(position)

    }

    // for clearing our selection
    @SuppressLint("NotifyDataSetChanged")
    fun clearSelection() {
        selectedItems!!.clear()
        notifyDataSetChanged()
    }

    //region All function for select
    /*
     This method will trigger when we we long press the item and it will
     change the icon of the item to check icon.
     */
    private fun toggleIcon(binding: CellAllCoursesSearchListBinding, position: Int) {
        if (selectedItems!![position, false]) {
            binding.courseImgCheck.visibility = View.VISIBLE
            if (selectedIndex == position) selectedIndex = -1
        } else {
            binding.courseImgCheck.visibility = View.GONE
            if (selectedIndex == position) selectedIndex = -1
        }
    }

    /*
      How many items have been selected? this method exactly the same. this will return a total
      number of selected items.
      */
    fun selectedItemCount(): Int {
        return selectedItems!!.size()
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

    override fun getItemCount(): Int {
        return courseList.size
    }
}

