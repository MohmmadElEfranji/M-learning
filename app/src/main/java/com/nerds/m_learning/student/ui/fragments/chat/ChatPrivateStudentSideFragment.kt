package com.nerds.m_learning.student.ui.fragments.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentChatPrivateStudentSideBinding
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.model.TextMessage
import com.nerds.m_learning.student.ui.fragments.home.StudentHomeFragment
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.ChatAdapter
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.ui.SharedViewModel
import java.util.*

class ChatPrivateStudentSideFragment : Fragment() {
    /*----------------------------------*/

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid

    /*----------------------------------*/

    private val collectionRefChatChannel =
        Firebase.firestore.collection(Teachers.COLLECTION_CHAT_CHANNEL)
    private val collectionRef = Firebase.firestore.collection(Students.COLLECTION_STUDENTS)
    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)

    /*----------------------------------*/

    private val viewModel: ChatPrivateStudentSideViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /*----------------------------------*/
    private val mTAG = "_ChatPrivateStudent"
    private lateinit var binding: FragmentChatPrivateStudentSideBinding
    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter()
    }

    private var chatMessagesList: MutableList<TextMessage> = java.util.ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_chat_private_student_side,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycler()


        /*    viewModel.getOfChatChannelForTeacher.observe(viewLifecycleOwner) {
                if (it.data != null) {
                    //   getAllCourses(it.data.chatChannelID)
                    getBookListRealTime(it.data.chatChannelID)
                    //   viewModel.getOfMessageChannel(it.data.chatChannelID)
                }
            }*/
        val arguments = arguments
        val course = arguments!!.getParcelable<Students.Courses>("course")

        course?.let {
            collectionRefTeachers.document(it.teacherID).get().addOnSuccessListener { doc ->
                if (doc != null) {
                    val emailTeacher = doc.get("email").toString()
                    binding.tvName.text = emailTeacher
                }
                getChatChannelForStudent(it.teacherID)


            }


            val ids: MutableList<String> = ArrayList()
            ids.add(course.teacherID)
            ids.add(studentID)

            //   viewModel.getChatChannelForStudent(studentID, course.teacherID)

            collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(course.teacherID).get().addOnSuccessListener { t ->
                    if (t.exists()) {
                        Log.d(mTAG, "onViewCreated: hi")
                        return@addOnSuccessListener
                    }
                    val channelID = collectionRefChatChannel.document().id
                    val chatChannel = Teachers.ChatChannel(channelID, ids)

                    viewModel.addOfChatChannel(channelID, chatChannel)

                    val channel = Teachers.ChatChannelForUser(channelID)

                    viewModel.addChatChannelForStudent(studentID, course.teacherID, channel)

                    viewModel.addChatChannelForTeacher(course.teacherID, studentID, channel)


                }



            binding.senderBtn.setOnClickListener {
                val message = binding.edMessage.text.toString()
                val date = Calendar.getInstance().time

                if (message.isNotEmpty()) {
                    val textMessage = TextMessage(message, date, studentID)

                    getChatChannelID(course.teacherID, textMessage)

                    /*  viewModel.getOfChatChannelForTeacher.observe(viewLifecycleOwner) { m ->
                          if (m.data != null) {
                              viewModel.addOfMessageChannel(m.data.chatChannelID, textMessage)
                          }
                      }*/

                    binding.edMessage.text.clear()
                }


            }
        }


    }

    private fun setUpRecycler() {
        val lm = LinearLayoutManager(requireActivity())
        binding.rvChatRecycler.layoutManager = lm
        lm.stackFromEnd = true

        binding.rvChatRecycler.adapter = chatAdapter

    }

    private fun getChatMessagesListRealTime(chatChannelID: String) {
        collectionRefChatChannel.document(chatChannelID)
            .collection("Messages")
            .orderBy("timeOfMessage").addSnapshotListener { value, error ->
                error?.let {
                    Log.w("TAG", "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {

                            val message = value.toObjects<TextMessage>() as MutableList<TextMessage>

                            chatAdapter.setChatList(message)
                            binding.rvChatRecycler.postDelayed({
                                binding.rvChatRecycler.smoothScrollToPosition(message.lastIndex)
                            }, 0)

                        }
                        else -> {

                            Log.d(mTAG, "getBookListRealTime: hi")
                        }
                    }
                }

            }
    }

    private fun getChatChannelID(teacherID: String, textMessage: TextMessage) {

        collectionRef.document(studentID)
            .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
            .document(teacherID).get().addOnSuccessListener {

                val chatChannelID = it.get("chatChannelID").toString()

                viewModel.addOfMessageChannel(chatChannelID, textMessage)
            }


    }


    private fun getChatChannelForStudent(teacherID: String) {
        collectionRef.document(studentID)
            .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
            .document(teacherID).addSnapshotListener { value, error ->
                error?.let {
                    Log.w("TAG", "Listen failed.", error)
                    return@addSnapshotListener
                }

                value?.let {

                    val student = value.toObject<Teachers.ChatChannelForUser>()

                    student?.let { it1 -> getChatMessagesListRealTime(it1.chatChannelID) }
                }

            }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callBack = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, StudentHomeFragment()).commit()

                sharedViewModel.mSelectedFragment.value = 6
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }
}