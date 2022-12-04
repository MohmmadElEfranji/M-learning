package com.nerds.m_learning.teacher.ui.fragments.lectures.edit_lecture

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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentEditLectureBinding
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_Lectures
import com.shasin.notificationbanner.Banner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream


class EditLectureFragment : Fragment() {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private var curFile: Uri? = null

    /*----------------------------------*/
    private lateinit var lecture: Teachers.LecturesOfCourse
    private lateinit var viewModel: EditLectureViewModel

    /*----------------------------------*/
    private lateinit var binding: FragmentEditLectureBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_lecture, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EditLectureViewModel::class.java]

        //region Fill in the lecture information
        val arg = this.arguments
        lecture = arg?.getParcelable("lecture")!!
        binding.edNewLectureName.setText(lecture.lectureName)
        binding.edNewLectureDescription.setText(lecture.lectureDescription)
        //endregion


        viewModel.editLecture.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                binding.progressBar.visibility = View.GONE
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The lecture has been successfully Edited :)", Banner.TOP, 3000
                ).show()

                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.courseContainerFragment, true).build()
                findNavController().navigate(
                    R.id.action_editLectureFragment_to_courseContainerFragment,
                    null,
                    navOptions
                )
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

                    binding.edLectureVideo.setText(getString(R.string.video_selected))
                }
            }
        /*----------------------------------*/

        binding.edLectureVideo.setOnClickListener {
            val intent = Intent()
                .setType("video/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }
        /*----------------------------------*/

        binding.btnEditLecture.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val mNewLectureMap = getNewLectureData()

            viewModel.editCourse(mNewLectureMap, teacherID, lecture.courseID, lecture.lectureID)
        }

    }

    private fun getNewLectureData(): Map<String, Any> {

        val lectureID = lecture.lectureID
        val lectureName = binding.edNewLectureName.text.toString()
        val lectureDescription = binding.edNewLectureDescription.text.toString()
        val lectureVideo = binding.edLectureVideo.text.toString()

        val map = mutableMapOf<String, Any>()

        if (lectureVideo == getString(R.string.video_selected)) {
            val file = getFile(requireContext(), curFile!!)
            val stream = FileInputStream(file)
            val uploadTask =
                storageRef.child("$CHILD_PATH_Lectures/${lecture.lectureVideo}")
                    .putStream(stream)

            uploadTask.addOnFailureListener { e ->
                Log.d("sss", "Fail ! ${e.message}")
            }.addOnSuccessListener { taskSnapshot ->
                Log.d("sss", "Done ! ${taskSnapshot.metadata?.sizeBytes}")

            }
        }
        if (lectureName.isNotEmpty() && lectureDescription.isNotEmpty()) {
            map["lectureID"] = lectureID
            map["lectureName"] = lectureName
            map["lectureDescription"] = lectureDescription


        } else {
            Toast.makeText(requireContext(), "Fill in all Fields !!", Toast.LENGTH_SHORT).show()
        }
        return map

    }

    //region FunctionForUploadImage
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
                    NavOptions.Builder().setPopUpTo(R.id.courseContainerFragment, true).build()
                findNavController().navigate(
                    R.id.action_editLectureFragment_to_courseContainerFragment,
                    null,
                    navOptions
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}