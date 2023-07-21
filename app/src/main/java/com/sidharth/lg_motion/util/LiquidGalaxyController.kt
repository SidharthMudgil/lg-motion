package com.sidharth.lg_motion.util

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream
import java.util.Properties


class LiquidGalaxyController(
    private val username: String,
    private val password: String,
    private val host: String,
    private val port: Int,
    private val screens: Int,
) {
    private val logoSlaves = "slave_${leftScreen}"
    private var session: Session? = null

    private val leftScreen: Int
        get() {
            return if (screens == 1) 1 else (screens / 2) + 2
        }

    private val rightScreen: Int
        get() {
            return if (screens == 1) 1 else (screens / 2) + 1
        }

    private val connected: Boolean
        get() {
            return session?.isConnected ?: false
        }

    init {
        setupNewSession()
    }

    private fun setupNewSession() {
        val jSch = JSch()
        session = jSch.getSession(username, host, port)
        session?.setPassword(password)
        val config = Properties()
        config["StrictHostKeyChecking"] = "no"
        session?.setConfig(config)
        session?.connect()
    }

    fun connect(): Boolean {
        if (session == null || connected.not()) {
            setupNewSession()
            displayLogos()
        } else {
            session?.sendKeepAliveMsg()
        }

        return true // TODO(return correct value using try-catch)
    }

    fun disconnect() {
        if (session != null) {
            session?.disconnect()
        }
        session = null
    }

    private fun execute(command: String) {
        if (session != null && connected) {
            val channel = session?.openChannel("exec") as ChannelExec
            channel.outputStream = ByteArrayOutputStream()
            channel.setCommand(command)
            channel.connect()
            channel.disconnect()
        }
    }

    private fun displayLogos() {
        val command = ""
        execute(command)
    }


    fun moveNorth() {
        val command = ""
        execute(command)
    }

    fun moveEast() {
        val command = ""
        execute(command)
    }

    fun moveSouth() {
        val command = ""
        execute(command)
    }

    fun moveWest() {
        val command = ""
        execute(command)
    }

    fun zoomIn() {
        val command = ""
        execute(command)
    }

    fun zoomOut() {
        val command = ""
        execute(command)
    }

    fun setRefresh() {
        val command = ""
        execute(command)
    }

    fun resetRefresh() {
        val command = ""
        execute(command)
    }

    fun clearKml() {
        val command = ""
        execute(command)
    }

    fun relaunch() {
        val command = ""
        execute(command)
    }

    fun restart() {
        val command = ""
        execute(command)
    }

    fun shutdown() {
        val command = ""
        execute(command)
    }
}