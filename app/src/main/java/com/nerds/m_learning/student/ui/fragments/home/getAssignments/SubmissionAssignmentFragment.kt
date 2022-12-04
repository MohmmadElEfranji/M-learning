package com.nerds.m_learning.student.ui.fragments.home.getAssignments

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentSubmissionAssignmentBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.ui.fragments.container.ContainerFragment
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class SubmissionAssignmentFragment : Fragment() {
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private var curFile: Uri? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: AssignmentStudentSideViewModel by activityViewModels()
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    private val studentEmail = auth.currentUser!!.email
    private lateinit var analytics: FirebaseAnalytics
    private val mTAG = "_SubmissionAssignment"
    private lateinit var binding: FragmentSubmissionAssignmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_submission_assignment,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics


        viewModel.addSubmissionAssignment.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                binding.progressBar.visibility = View.GONE
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The Assignment has been successfully added :)", Banner.TOP, 3000
                ).show()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ContainerFragment()).commit()
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

                    binding.edAssignment.setText(getString(R.string.file_selected))
                }
            }
        /*----------------------------------*/

        binding.edAssignment.setOnClickListener {
            val intent = Intent()
                .setType("application/pdf")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }
        /*----------------------------------*/


        binding.btnAdd.setOnClickListener {
            addSubmissionAssignment()
            binding.progressBar.visibility = View.VISIBLE

        }
    }

    private fun addSubmissionAssignment() {
        val file = getFile(requireContext(), curFile!!)
        val videosRef = storageRef.child(Teachers.CHILD_PATH_Submissions_ASSIGNMENT_OF_Lectures)
        val fileName = file.name + Calendar.getInstance().time
        val spaceRef = videosRef.child(fileName)
        val stream = FileInputStream(file)

        val uploadTask = spaceRef.putStream(stream)


        uploadTask.addOnFailureListener { e ->

            Log.d("sss", "Fail ! ${e.message}")

        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                val assignment = Students.SubmissionAssignmentsOfLecture(
                    studentID,
                    studentEmail,
                    uri.toString(),
                    fileName
                )
                sharedViewModel.sharedAssignmentID.observe(viewLifecycleOwner) { task ->
                    viewModel.addSubmissionAssignment(
                        assignment,
                        task.teacherID,
                        studentID,
                        task.courseID,
                        task.lectureID,
                        task.assignmentID
                    )
                    val params = Bundle()
                    params.putString("addSubmissionAssignment", task.assignmentName)
                    analytics.logEvent("addSubmissionAssignment", params)
                }

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

        val callBack = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ContainerFragment()).commit()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }
}