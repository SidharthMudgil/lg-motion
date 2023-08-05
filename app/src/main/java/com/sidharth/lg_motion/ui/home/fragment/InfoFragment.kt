package com.sidharth.lg_motion.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.sidharth.lg_motion.databinding.InfoFaceGesturesBinding
import com.sidharth.lg_motion.databinding.InfoVoiceCommandsBinding
import com.sidharth.lg_motion.util.Info

class InfoFragment : Fragment() {
    private val infoArgs: InfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = when (infoArgs.info) {
            Info.FACE_GESTURES -> InfoFaceGesturesBinding.inflate(inflater)
            Info.VOICE_COMMANDS -> InfoVoiceCommandsBinding.inflate(inflater)
            Info.HAND_GESTURES -> InfoFaceGesturesBinding.inflate(inflater)
            Info.BODY_POSES -> InfoFaceGesturesBinding.inflate(inflater)
            Info.OBJECTS -> InfoFaceGesturesBinding.inflate(inflater)
        }
        return binding.root
    }
}