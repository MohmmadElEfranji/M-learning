package com.nerds.m_learning.teacher.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.common_ui.signIn.SignInActivity
import com.nerds.m_learning.databinding.FragmentHomeTeacherBinding
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeViewModel
import com.nerds.m_learning.student.ui.fragments.home.getLectures.LecturesStudentSideViewModel
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.CoursesAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.nerds.m_learning.teacher.ui.fragments.lectures.get_lectures.LecturesViewModel
import com.nerds.m_learning.teacher.ui.fragments.students_of_course.CourseStudentsViewModel
import com.shasin.notificationbanner.Banner


class HomeFragment : Fragment(), CoursesAdapter.OnCourseReceived, CoursesAdapter.OnClick {
    /*----------------------------------*/
    private lateinit var viewModel: HomeViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val courseStudentsViewModel: CourseStudentsViewModel by activityViewModels()
    private val lecturesViewModel: LecturesViewModel by activityViewModels()
    private val studentHomeViewModel: StudentHomeViewModel by activityViewModels()
    private val lecturesStudentSideViewModel: LecturesStudentSideViewModel by activityViewModels()


    /*----------------------------------*/
    private val coursesAdapter: CoursesAdapter by lazy {
        CoursesAdapter(requireContext(), this, this)
    }

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val mTAG: String = "_HomeFragment"
    private lateinit var binding: FragmentHomeTeacherBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_teacher, container, false)
        return binding.root
    }
    /*----------------------------------*/

    @SuppressLint("DiscouragedPrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/


        binding.coursesAdapter = coursesAdapter

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.btnMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.popup_menu3, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.action_logOut -> {
                        auth.signOut()
                        val intent = Intent(requireContext(), SignInActivity::class.java)
                        startActivity(intent)

                        requireActivity().finish()

                    }

                }
                true
            }

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
            //popupMenu.show()
        }
        /*----------------------------------*/
        binding.swipeContainer.setOnRefreshListener {

            viewModel.getAllCourses(teacherID)
            binding.swipeContainer.isRefreshing = false
        }
        binding.swipeContainer.setColorSchemeResources(R.color.new_color2)
        /*----------------------------------*/
        viewModel.allCourses.observe(viewLifecycleOwner) { task ->
            if (task.data != null) {
                coursesAdapter.submitList(task.data)

                if (task.data.isEmpty()) {
                    binding.imgEmpty.visibility = View.VISIBLE
                } else if (task.data.isNotEmpty()) {
                    binding.imgEmpty.visibility = View.GONE
                }
            } else {
                Log.d(mTAG, "StudentHomeFragment: getAllCourses2 => ${task.message}")
            }
        }

        viewModel.deleteCourse.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The course has been successfully Deleted :)", Banner.TOP, 2000
                ).show()
                viewModel.getAllCourses(teacherID)

            } else {
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "${task.message} :(", Banner.TOP, 2000
                ).show()
            }
        }

        viewModel.getAllCourses(teacherID)

        /*----------------------------------*/
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addCourseFragment)
        }
        /*----------------------------------*/


    }


    override fun onCourseReceived(passCourseID: Teachers.PassCourseID) {
        sharedViewModel.publishID(passCourseID)
        findNavController().navigate(
            R.id.action_homeFragment_to_courseContainerFragment
        )
    }

    override fun onClick(course: Teachers.Courses, menu: View) {
        val popupMenu = PopupMenu(requireContext(), menu)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        if (course.visibility == false) {
            popupMenu.menu.getItem(1).title = "UnHide"
        } else if (course.publishing == true) {
            popupMenu.menu.getItem(3).title = "Published"
        }
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.action_edit -> {

                    val bundle = Bundle()
                    bundle.putParcelable("course", course)

                    findNavController().navigate(
                        R.id.action_homeFragment_to_editCourseFragment,
                        bundle
                    )
                }
                R.id.action_hide -> {
                    if (course.publishing == true){
                        updateCourseVisibilityState(course)
                    }else if(course.publishing == false){
                        Banner.make(
                            binding.root, requireActivity(), Banner.WARNING,
                            "The course must be public first!!", Banner.TOP, 2000
                        ).show()
                    }

                }

                R.id.action_delete -> {
                    viewModel.deleteCourse(teacherID, course.courseID, course.courseNameImage)
                }
                R.id.action_publishing -> {
                    if (course.publishing == false) {
                        lecturesViewModel.getAllLecturesOfCourse(teacherID, course.courseID)
                        lecturesViewModel.allLectures.observe(viewLifecycleOwner) {
                            val array = it.data!!

                            if (array.size >= 5) {
                                viewModel.updateCoursePublishingState(course)
                                viewModel.allCourses.removeObservers(viewLifecycleOwner)

                            } else {
                                Banner.make(
                                    binding.root, requireActivity(), Banner.WARNING,
                                    "The course must contain at least five lectures!!", Banner.TOP, 2000
                                ).show()
                                viewModel.allCourses.removeObservers(viewLifecycleOwner)
                            }
                        }

                    }
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun updateCourseVisibilityState(course: Teachers.Courses) {
        courseStudentsViewModel.getAllStudentsOfCourse(teacherID, course.courseID)
        if (course.visibility == false) {
            courseStudentsViewModel.allStudentsOfCourse.observe(viewLifecycleOwner) {
                for (i in it.data!!) {
                    courseStudentsViewModel.updateCourseVisibilityStateFromStudents(
                        i.StudentID,
                        course, true
                    )
                }
            }
            viewModel.updateCourseVisibilityState(course, true)


        } else if (course.visibility == true) {
            courseStudentsViewModel.allStudentsOfCourse.observe(viewLifecycleOwner) {
                for (i in it.data!!) {
                    courseStudentsViewModel.updateCourseVisibilityStateFromStudents(
                        i.StudentID,
                        course, false
                    )
                }
            }
            viewModel.updateCourseVisibilityState(course, false)
        }
    }

}