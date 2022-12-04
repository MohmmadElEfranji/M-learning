package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.get_assignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentAssignmentBinding
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.AssignmentAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner

class AssignmentFragment : Fragment(), AssignmentAdapter.OnClick {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: AssignmentViewModel

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val assignmentAdapter: AssignmentAdapter by lazy {
        AssignmentAdapter(this)
    }

    /*----------------------------------*/
    private val mTAG = "_AssignmentFragment"
    private lateinit var binding: FragmentAssignmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_assignment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AssignmentViewModel::class.java]

        binding.swipeContainer.setOnRefreshListener {

            sharedViewModel.sharedID.observe(viewLifecycleOwner) { task ->

                viewModel.getAllAssignmentsOfLecture(teacherID, task.courseID, task.lectureID)

            }
            binding.swipeContainer.isRefreshing = false
        }
        binding.swipeContainer.setColorSchemeResources(R.color.new_color2)
        viewModel.allAssignments.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {

                assignmentAdapter.submitList(task.data)

                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "AssignmentFragment: allAssignments => ${task.message}")
            }
        }

        viewModel.deleteAssignment.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The assignment has been successfully Deleted :)", Banner.TOP, 2000
                ).show()
                sharedViewModel.sharedID.observe(viewLifecycleOwner) { task2 ->

                    viewModel.getAllAssignmentsOfLecture(teacherID, task2.courseID, task2.lectureID)

                }

            } else {

                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "${task.message} :(", Banner.TOP, 2000
                ).show()

            }
        }
        sharedViewModel.sharedID.observe(viewLifecycleOwner) { task ->

            viewModel.getAllAssignmentsOfLecture(teacherID, task.courseID, task.lectureID)

        }
        setUpRecycler()
        binding.fabAddAssignment.setOnClickListener {
            findNavController().navigate(R.id.action_lectureContainerFragment_to_addAssignmentFragment)

        }

    }

    private fun setUpRecycler() {
        binding.rvAssignments.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvAssignments.adapter = assignmentAdapter
    }

    override fun onClick(assignment: Teachers.AssignmentsOfLecture, menu: View) {
        val popupMenu = PopupMenu(requireContext(), menu)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        if (assignment.visibility == false) {
            popupMenu.menu.getItem(1).title = "UnHide"
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {

                    val bundle = Bundle()
                    bundle.putParcelable("assignment", assignment)

                    findNavController().navigate(
                        R.id.action_lectureContainerFragment_to_editAssignmentFragment,
                        bundle
                    )
                }
                R.id.action_hide -> {
                    if (assignment.visibility == false) {
                        viewModel.updateAssignmentVisibilityState(assignment, true)
                    } else if (assignment.visibility == true) {
                        viewModel.updateAssignmentVisibilityState(assignment, false)
                    }
                }
                R.id.action_delete -> {
                    sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task ->
                        viewModel.deleteAssignment(
                            teacherID,
                            task.courseID,
                            assignment.lectureID,
                            assignment.assignmentID,
                            assignment.assignmentFileName
                        )
                    }

                }


            }
            true
        }


        popupMenu.show()
    }

    override fun onClickSubmissions(assignment: Teachers.AssignmentsOfLecture) {
        val bundle = Bundle()
        bundle.putParcelable("assignment", assignment)
        findNavController().navigate(
            R.id.action_lectureContainerFragment_to_allSubmissionsFragment,
            bundle
        )
    }

    override fun onClickShow(assignment: Teachers.AssignmentsOfLecture) {
        val bundle = Bundle()
        bundle.putParcelable("assignment", assignment)
        findNavController().navigate(
            R.id.action_lectureContainerFragment_to_showAssignmentFragment,
            bundle
        )
    }

}