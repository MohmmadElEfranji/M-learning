package com.nerds.m_learning.student.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentCustomDialogBinding
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeViewModel
import com.nerds.m_learning.teacher.ui.SharedViewModel

class InterestsDialogFragment : DialogFragment() {
    private val viewModel: StudentHomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var actionModeCallback: ActionMode.Callback? = null

    private val array: MutableList<String> = ArrayList()

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    private lateinit var binding: FragmentCustomDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_custom_dialog, container, false)
        val fields = resources.getStringArray(R.array.Fields)
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_multiple_choice,
            fields
        )
        binding.listViewData.adapter = arrayAdapter

        viewModel.getAllTheNamesOfTheCoursesInterestedIn(studentID)
        viewModel.getAllTheNamesOfTheCoursesInterestedIn.observe(viewLifecycleOwner) {
            val mInterests = it.data!!.interests
            for (i in mInterests) {
                val j = arrayAdapter.getPosition(i)
                binding.listViewData.setItemChecked(j, true)
            }
            mInterests.clear()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }


        binding.btnSubmit.setOnClickListener {
            for (i in 0..binding.listViewData.count) {
                if (binding.listViewData.isItemChecked(i)) {
                    array.add(binding.listViewData.getItemAtPosition(i).toString())
                }
            }
            viewModel.updateInterests(studentID, array)
            dismiss()

        }

        return binding.root
    }


}