package com.nerds.m_learning.teacher.ui.fragments.lectures.chapters.edit_chapters

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentEditChapterBinding
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_CHAPTERS_OF_Lectures
import com.nerds.m_learning.teacher.ui.SharedViewModel
import com.shasin.notificationbanner.Banner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class EditChapterFragment : Fragment() {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private var curFile: Uri? = null

    /*----------------------------------*/
    private lateinit var chapter: Teachers.ChapterOfLecture
    private lateinit var viewModel: EditChapterViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /*----------------------------------*/
    private lateinit var binding:FragmentEditChapterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_chapter, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[EditChapterViewModel::class.java]
        /*----------------------------------*/
        //region Fill in the lecture information
        val arg = this.arguments
        chapter = arg?.getParcelable("chapter")!!
        binding.edNewChapterName.setText(chapter.chapterName)
        binding.edNewChapterDescription.setText(chapter.chapterDescription)
        //endregion



        viewModel.editChapter.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                binding.progressBar.visibility = View.GONE
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The Chapter has been successfully Edited :)", Banner.TOP, 3000
                ).show()
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.lectureContainerFragment, true).build()
                findNavController().navigate(
                    R.id.action_editChapterFragment_to_lectureContainerFragment,
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

                    binding.edNewChapterFile.setText(getString(R.string.file_selected))
                }
            }
        /*----------------------------------*/

        binding.edNewChapterFile.setOnClickListener {
            val intent = Intent()
                .setType("application/pdf")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }
        /*----------------------------------*/

        binding.btnAddChapter.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val newChapterMap = getNewChapterData()

            sharedViewModel.sharedCourseID.observe(viewLifecycleOwner) { task2 ->
                viewModel.editChapter(
                    newChapterMap,
                    teacherID,
                    task2.courseID,
                    chapter.lectureID,
                    chapter.chapterID
                )
            }
        }
    }

    private fun getNewChapterData(): Map<String, Any> {

        val chapterID = chapter.chapterID
        val chapterName = binding.edNewChapterName.text.toString()
        val chapterDescription = binding.edNewChapterDescription.text.toString()
        val chapterFile = binding.edNewChapterFile.text.toString()

        val map = mutableMapOf<String, Any>()

        if (chapterFile == getString(R.string.file_selected)) {
            val file = getFile(requireContext(), curFile!!)
            val stream = FileInputStream(file)
            val uploadTask =
                storageRef.child("$CHILD_PATH_CHAPTERS_OF_Lectures/${chapter.chapterFile}")
                    .putStream(stream)

            uploadTask.addOnFailureListener { e ->
                Log.d("sss", "Fail ! ${e.message}")
            }.addOnSuccessListener { taskSnapshot ->
                Log.d("sss", "Done ! ${taskSnapshot.metadata?.sizeBytes}")

            }
        }
        if (chapterName.isNotEmpty() && chapterDescription.isNotEmpty()) {
            map["chapterID"] = chapterID
            map["chapterName"] = chapterName
            map["chapterDescription"] = chapterDescription


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
                    NavOptions.Builder().setPopUpTo(R.id.lectureContainerFragment, true).build()
                findNavController().navigate(
                    R.id.action_editChapterFragment_to_lectureContainerFragment,
                    null,
                    navOptions
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}