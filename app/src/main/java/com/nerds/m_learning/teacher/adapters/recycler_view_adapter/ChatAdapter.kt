package com.nerds.m_learning.teacher.adapters.recycler_view_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.student.model.TextMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val currentID = auth.currentUser!!.uid

    private var chatMessages: MutableList<TextMessage> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setChatList(itemsCells: MutableList<TextMessage>) {
        this.chatMessages = itemsCells
        notifyDataSetChanged()

    }


    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private lateinit var tvSender: TextView
        private lateinit var tvReceiver: TextView
        private lateinit var tvDate: TextView


        private fun sender(item: TextMessage) {
            tvSender = itemView.findViewById(R.id.tv_outGoing)
            tvSender.text = item.message

            tvDate = itemView.findViewById(R.id.tvDateSender)
            tvDate.text = android.text.format.DateFormat.format("yyyy/MM/dd hh:mm a", item.timeOfMessage)
        }


        private fun receiver(item: TextMessage) {
            tvReceiver = itemView.findViewById(R.id.tv_incoming)
            tvReceiver.text = item.message


            tvDate = itemView.findViewById(R.id.tvDateReceiver)
            tvDate.text = android.text.format.DateFormat.format("yyyy/MM/dd hh:mm a", item.timeOfMessage)

        }


        private fun chat(item: TextMessage) {

            if (item.senderID == currentID) {
                sender(item)
            } else {
                receiver(item)
            }
        }

        fun bind(dataModel: TextMessage) = when {
            else -> chat(dataModel)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {


        val layout = when (viewType) {
            TYPE_SENDER -> R.layout.msg_outgoing
            TYPE_REC -> R.layout.msg_incoming

            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ItemViewHolder(view.rootView)


    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.bind(chatMessages[position])


    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)

        val c = chatMessages[position]
        return if (c.senderID == currentID) {
            TYPE_SENDER
        } else {
            TYPE_REC
        }

    }


    override fun getItemCount(): Int {
        return chatMessages.size
    }

    companion object {
        private const val TYPE_SENDER = 1
        private const val TYPE_REC = 2

    }


}