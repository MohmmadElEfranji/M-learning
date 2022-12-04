package com.nerds.m_learning.teacher.ui.fragments.courses.add_course

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
import android.widget.ArrayAdapter
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentAddCourseBinding
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_COURSES
import com.nerds.m_learning.teacher.model.Teachers.Companion.COLLECTION_ALL_COURSES
import com.nerds.m_learning.teacher.model.Teachers.Companion.SUB_COLLECTION_COURSES
import com.shasin.notificationbanner.Banner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*


class AddCourseFragment : Fragment() {


    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)
    private val collectionRefAllCourses = Firebase.firestore.collection(COLLECTION_ALL_COURSES)
    /*----------------------------------*/

    private lateinit var viewModel: AddCourseViewModel

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private var curFile: Uri? = null

    /*----------------------------------*/
    private val mTAG: String = "_AddCourseFragment"
    private lateinit var binding: FragmentAddCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_course, container, false)
        val fields = resources.getStringArray(R.array.Fields)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fields)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        return binding.root
    }
    /*----------------------------------*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this)[AddCourseViewModel::class.java]

        viewModel.addCourse.observe(viewLifecycleOwner) { task ->
            if (task.data == true) {
                binding.progressBar.visibility = View.GONE
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "The course has been successfully added :)", Banner.TOP, 3000
                ).show()
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                findNavController().navigate(
                    R.id.action_addCourseFragment_to_homeFragment,
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

                    binding.edCourseCover.setText(getString(R.string.image_selected))
                }
            }
        /*----------------------------------*/

        binding.edCourseCover.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }
        /*----------------------------------*/

        binding.btnAddCourse.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            addCourseMVVM()

        }
    }

    private fun addCourseMVVM() {
        val courseId = collectionRefTeachers.document(teacherID).collection(SUB_COLLECTION_COURSES)
            .document().id
        val courseName = binding.edCourseName.text.toString()
        val courseDescription = binding.edCourseDescription.text.toString()
        val courseField = binding.autoCompleteTextView.text.toString()
        val courseImage = binding.edCourseCover.text.toString()

        if (courseName.isNotEmpty() && courseField.isNotEmpty() && courseDescription.isNotEmpty()
            && courseImage == getString(R.string.image_selected)
        ) {

            val file = getFile(requireContext(), curFile!!)
            val imagesRef = storageRef.child(CHILD_PATH_COURSES)
            val fileName = file.name + Calendar.getInstance().time
            val spaceRef = imagesRef.child(fileName)
            val stream = FileInputStream(file)

            val uploadTask = spaceRef.putStream(stream)


            uploadTask.addOnFailureListener { e ->

                Log.d("sss", "Fail ! ${e.message}")

            }.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->

                    val course = Teachers.Courses(
                        courseId,
                        teacherID,
                        courseName,
                        courseField,
                        courseDescription,
                        uri.toString(),
                        fileName,
                        search_key = courseName.lowercase(Locale.getDefault())

                    )

                    viewModel.addCourse(course)

                    //viewModel.addOfAllCourse(course)

                    Log.d(mTAG, "uploadBookCoverToStorage: $uri")
                }
                Log.d("sss", "Done ! ${taskSnapshot.metadata?.sizeBytes}")
            }


        } else {
            Toast.makeText(requireContext(), "Fill All", Toast.LENGTH_SHORT).show()

        }


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
                    NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                findNavController().navigate(
                    R.id.action_addCourseFragment_to_homeFragment,
                    null,
                    navOptions
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


}