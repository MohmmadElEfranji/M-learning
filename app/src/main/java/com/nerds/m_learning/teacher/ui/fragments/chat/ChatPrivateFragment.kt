package com.nerds.m_learning.teacher.ui.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentChatPrivateBinding
import com.nerds.m_learning.student.model.TextMessage
import com.nerds.m_learning.teacher.adapters.recycler_view_adapter.ChatAdapter
import com.nerds.m_learning.teacher.model.Teachers
import java.util.*


class ChatPrivateFragment : Fragment() {
    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val teacherID = auth.currentUser!!.uid

    /*----------------------------------*/
    private val collectionRefChatChannel =
        Firebase.firestore.collection(Teachers.COLLECTION_CHAT_CHANNEL)
    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)

    /*----------------------------------*/
    private val viewModel: ChatPrivateViewModel by activityViewModels()
    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter()
    }

    /*----------------------------------*/
    private val mTAG = "_ChatPrivateFragment"
    private lateinit var binding: FragmentChatPrivateBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chat_private, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/
        setUpRecycler()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_chatPrivateFragment_to_courseContainerFragment)
        }


        /*    viewModel.getOfChatChannelForTeacher.observe(viewLifecycleOwner) {
                if (it.data != null) {
                    //   getAllCourses(it.data.chatChannelID)
                    getBookListRealTime(it.data.chatChannelID)
                    return@observe
                    //   viewModel.getOfMessageChannel(it.data.chatChannelID)
                }
            }*/

        val arg = this.arguments
        val student = arg?.getParcelable<Teachers.CourseStudents>("student")


        binding.tvName.text = student!!.StudentEmail
        getChatChannelForStudent(student.StudentID)
        val ids: MutableList<String> = ArrayList()
        ids.add(student.StudentID)
        ids.add(teacherID)

        //viewModel.getChatChannelForTeacher(teacherID, student.StudentID)

        collectionRefTeachers.document(teacherID)
            .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
            .document(student.StudentID).get().addOnSuccessListener {
                if (it.exists()) {
                    Log.d(mTAG, "onViewCreated: hi")
                    return@addOnSuccessListener
                }
                val channelID = collectionRefChatChannel.document().id
                val chatChannel = Teachers.ChatChannel(channelID, ids)

                viewModel.addOfChatChannel(channelID, chatChannel)

                val channel = Teachers.ChatChannelForUser(channelID)
                viewModel.addChatChannelForTeacher(teacherID, student.StudentID, channel)

                viewModel.addChatChannelForStudent(student.StudentID, teacherID, channel)
            }


        binding.senderBtn.setOnClickListener {
            val message = binding.edMessage.text.toString()
            val date = Calendar.getInstance().time

            if (message.isNotEmpty()) {
                val textMessage = TextMessage(message, date, teacherID)

                // viewModel.getChatChannelForTeacher(teacherID, student.StudentID)
                getChatChannelID(student.StudentID, textMessage)

                /*   viewModel.getOfChatChannelForTeacher.observe(viewLifecycleOwner) {
                       if (it.data != null) {
                           viewModel.addOfMessageChannel(it.data.chatChannelID, textMessage)
                       }
                   }*/

                binding.edMessage.text.clear()
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


    private fun getChatChannelForStudent(StudentID: String) {
        collectionRefTeachers.document(teacherID)
            .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
            .document(StudentID).addSnapshotListener { value, error ->
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

    private fun getChatChannelID(studentID: String, textMessage: TextMessage) {

        collectionRefTeachers.document(teacherID)
            .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
            .document(studentID).get().addOnSuccessListener {

                val chatChannelID = it.get("chatChannelID").toString()

                viewModel.addOfMessageChannel(chatChannelID, textMessage)
            }
     }
}