package com.nerds.m_learning.student.ui.fragments.home.getLectures

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
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.R
import com.nerds.m_learning.databinding.FragmentVideoShowBinding
import com.nerds.m_learning.teacher.ui.SharedViewModel


class VideoShowFragment : Fragment(), Player.Listener {
    /*----------------------------------*/
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: LecturesStudentSideViewModel
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val studentID = auth.currentUser!!.uid
    /*----------------------------------*/
    private val mTAG = "_VideoShowFragment"
    private lateinit var binding: FragmentVideoShowBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_show, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*----------------------------------*/
        viewModel = ViewModelProvider(this)[LecturesStudentSideViewModel::class.java]
        /*----------------------------------*/
        setupPlayer()

        sharedViewModel.sharedLectureID.observe(viewLifecycleOwner) { task ->

            viewModel.getLectureOfCourse(studentID, task.courseID, task.lectureID)

        }
        viewModel.lecture.observe(viewLifecycleOwner) { task ->
            task.data?.let {
                addMP4Files(it.lectureVideo!!)
                binding.tvVideoDescription?.text = it.lectureDescription

            }
            Log.d(mTAG, "StudentHomeFragment: getAllCourses2 => ${task.message}")

        }

        savedInstanceState?.let {
            savedInstanceState.getInt("mediaItem").let { restoredMedia ->
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMedia, seekTime)
                player.play()

            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("SeekTime", player.currentPosition)
        outState.putInt("mediaItem", player.currentMediaItemIndex)
    }
    private fun setupPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        playerView = binding.videoView
        playerView.player = player
        player.addListener(this)

    }

    private fun addMP4Files(uri: String) {
        val mediaItem =
            MediaItem.fromUri(uri)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when (playbackState) {
            Player.STATE_BUFFERING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                binding.progressBar.visibility = View.INVISIBLE

            }
            else -> {
                Log.d(mTAG, "onPlaybackStateChanged: somthing error")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callBack = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, LecturesStudentSideFragment()).commit()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }
}