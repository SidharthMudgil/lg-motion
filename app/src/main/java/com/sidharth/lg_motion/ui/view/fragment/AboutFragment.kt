package com.sidharth.lg_motion.ui.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sidharth.lg_motion.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {
    enum class Action(
        val url: String
    ) {
        DEV_EMAIL("mailto:smudgil101@gmail.com"),
        DEV_GITHUB("https://github.com/SidharthMudgil/"),
        DEV_LINKEDIN("https://www.linkedin.com/in/sidharthmudgil/"),
        DEV_STACKOVERFLOW("https://stackoverflow.com/users/16177121/sidharth-mudgil"),
        ORG_WEBSITE("https://www.liquidgalaxy.eu/"),
        ORG_EMAIL("mailto:liquidgalaxylab@gmail.com"),
        ORG_GITHUB("https://github.com/LiquidGalaxyLAB/"),
        ORG_DISCORD("https://discord.gg/peGA5K8tJU"),
        ORG_LINKEDIN("https://www.linkedin.com/company/google-summer-of-code---liquid-galaxy-project/about/"),
        ORG_TWITTER("https://twitter.com/_liquidgalaxy"),
        ORG_INSTAGRAM("https://www.instagram.com/_liquidgalaxy/"),
        ORG_PLAYSTORE("https://play.google.com/store/apps/developer?id=Liquid+Galaxy+LAB"),
    }

    private val appDescription = """
        The incredible app designed to elevate your Liquid Galaxy experience like never before! With a seamless connection to your Liquid Galaxy rig, you can now control it remotely using facial expressions, voice commands, hand gestures, poses, and even object manipulation all at your fingertips!
        
        But we didn't stop there! Brace yourself for boundless fun as you enjoy playing iconic games like Tetris, Pacman, and more – all running on your Liquid Galaxy rig, controlled effortlessly through our app's intuitive features.
        
        Step into a world of endless excitement and adventure with LG Motion. Your Liquid Galaxy exploration has just been taken to a whole new level. Experience the magic today!
        
        Disclaimer: Please be informed that some images utilized in this app are generated using DALL-E2, an advanced AI model. All generated images are for illustrative purposes only and not intended to depict real-world entities or individuals. 
        """.trimIndent().trimMargin()

    private val orgDescription = """
        Liquid Galaxy is a remarkable panoramic system that is tremendously compelling. It started off as a Google 20% project created by Google engineer Jason Holt to run Google Earth across a cluster of PC's and it has grown from there!

        Liquid Galaxy hardware consists of 3 or more computers driving multiple displays, usually one computer for each display. Liquid Galaxy applications have been developed using a master/slave architecture. The view orientation of each slave display is configured in reference to the view of the master display. Navigation on the system is done from the master instance and the location on the master is broadcast to the slaves over UDP. The slave instances, knowing their own locations in reference to the master, then change their views accordingly. 

        The Liquid Galaxy Project, while making use of Google Earth software, does not develop the Google Earth code-base itself. Google Earth is not open source software, although it is free (as in beer). Instead, the Liquid Galaxy Project works on extending the Liquid Galaxy system with open source software both improving its administration and enabling open source applications, so that content of various types can be displayed in the immersive panoramic environment.
    """.trimIndent().trimMargin()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(inflater)

        binding.devEmail.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.DEV_EMAIL)
        }

        binding.devGithub.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.DEV_GITHUB)
        }

        binding.devLinkedin.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.DEV_LINKEDIN)
        }

        binding.devStackoverflow.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.DEV_STACKOVERFLOW)
        }

        binding.orgWebsite.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_WEBSITE)
        }

        binding.orgEmail.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_EMAIL)
        }

        binding.orgGithub.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_GITHUB)
        }

        binding.orgDiscord.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_DISCORD)
        }

        binding.orgLinkedin.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_LINKEDIN)
        }

        binding.orgTwitter.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_TWITTER)
        }

        binding.orgInstagram.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_INSTAGRAM)
        }

        binding.orgPlaystore.setOnClickListener {
            openUrlInAppOrEmail(requireContext(), Action.ORG_PLAYSTORE)
        }

        val version = "version ${
            requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            ).versionName
        }"

        binding.appDescription.text = appDescription
        binding.orgDescription.text = orgDescription
        binding.appVersion.text = version

        return binding.root
    }

    private fun openUrlInAppOrEmail(context: Context, action: Action) {
        val intent: Intent = when (action) {
            Action.DEV_EMAIL, Action.ORG_EMAIL -> {
                Intent(Intent.ACTION_SENDTO, Uri.parse(action.url)).apply {
                    putExtra(Intent.EXTRA_SUBJECT, "Subject for the email")
                }
            }

            else -> Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
        }
        context.startActivity(intent)
    }
}