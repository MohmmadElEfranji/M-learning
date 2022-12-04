package com.nerds.m_learning.teacher.ui.fragments.lectures.assignments.add_assignment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentAddAssignmentBinding
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_ASSIGNMENT_OF_Lectures
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class AddAssignmentFragment : Fragment() {


    /*----------------------------------*/
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private var curFile: Uri? = null

    /*----------------------------------*/
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: AddAssignmentViewModel

    /*----------------------------------*/
    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private lateinit var binding: FragmentAddAssignmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_assignment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AddAssignmentViewModel::class.java]

        viewModel.addAssignment.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                binding.progressBar.visibility = View.GONE
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The Assignment has been successfully added :)", Banner.TOP, 3000
                ).show()
                findNavController().navigate(R.id.action_addAssignmentFragment_to_lectureContainerFragment)

            } else {
                Banner.make(
                    binding.root, requireActivity(), Banner.ERROR,
                    "${task.message} :(", Banner.TOP, 3000
                ).show()
            }
        }
        /*----------------------------------*/
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent: Intent? = result.data
                    val uri = intent?.data
                    curFile = uri

                    binding.edAssignmentFile.setText(getString(R.string.file_selected))
                }
            }
        /*----------------------------------*/

        binding.edAssignmentFile.setOnClickListener {
            val intent = Intent()
                .setType("application/pdf")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }
        /*----------------------------------*/
        binding.btnAddAssignment.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            sharedViewModel.sharedID.observe(viewLifecycleOwner) { ID ->
                addAssignment(ID.courseID, ID.lectureID)
            }

        }

    }

    private fun addAssignment(courseID: String, lectureID: String) {
        val assignmentID = collectionRefTeachers.document(teacherID)
            .collection(Teachers.SUB_COLLECTION_COURSES)
            .document(courseID).collection(Teachers.SUB_COLLECTION_LECTURES)
            .document(lectureID)
            .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
            .document().id
        val assignmentName = binding.edAssignmentName.text.toString()
        val assignmentDescription = binding.edAssignmentDescription.text.toString()

        val file = getFile(requireContext(), curFile!!)
        val assignmentRef = storageRef.child(CHILD_PATH_ASSIGNMENT_OF_Lectures)
        val fileName = file.name + Calendar.getInstance().time
        val spaceRef = assignmentRef.child(fileName)
        val stream = FileInputStream(file)

        val uploadTask = spaceRef.putStream(stream)


        uploadTask.addOnFailureListener { e ->

            Log.d("sss", "Fail ! ${e.message}")

        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                val assignment = Teachers.AssignmentsOfLecture(
                    assignmentID,
                    lectureID,
                    teacherID,
                    courseID,
                    assignmentName,
                    assignmentDescription,
                    uri.toString(),
                    fileName
                )

                viewModel.addAssignment(assignment, teacherID, courseID, lectureID)

                Log.d("mTAG", "uploadBookCoverToStorage: $uri")
            }
            Log.d("sss", "Done ! ${taskSnapshot.metadata?.sizeBytes}")
        }


    }

    //region FunctionForUploadVideo
    private fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    //endregion
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.addAssignmentFragment, true).build()
                findNavController().navigate(
                    R.id.action_addAssignmentFragment_to_lectureContainerFragment,
                    null,
                    navOptions
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}