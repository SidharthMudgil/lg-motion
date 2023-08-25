package com.sidharth.lg_motion.util

import com.google.android.gms.maps.model.CameraPosition
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Properties


class LiquidGalaxyManager(
    private val username: String,
    private val password: String,
    private val host: String,
    private val port: Int,
    private val screens: Int,
) {
    enum class State(val key: String) {
        IDLE("idle"), FLY_TO("idle"), PLANET("idle"),
        MOVE_NORTH("Up"), MOVE_SOUTH("Down"), MOVE_EAST("Right"), MOVE_WEST("Left"),
        ROTATE_LEFT("ctrl+Left"), ROTATE_RIGHT("ctrl+Right"),
        ZOOM_IN("equal"), ZOOM_OUT("minus"),
    }

    private var lastState = State.IDLE
    private val logoSlave = "slave_${leftScreen}"
    private var session: Session? = null

    private val leftScreen: Int
        get() {
            return if (screens == 1) 1 else (screens / 2) + 2
        }

    val connected: Boolean
        get() {
            return session?.isConnected ?: false
        }

    private suspend fun setupNewSession(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jSch = JSch()
                session = jSch.getSession(username, host, port)
                session?.setPassword(password)
                val config = Properties()
                config["StrictHostKeyChecking"] = "no"
                session?.setConfig(config)
                session?.connect(7000)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun connect(): Boolean {
        return try {
            if (session == null || connected.not()) {
                if (setupNewSession()) {
                    displayLogos()
                } else {
                    return false
                }
            } else {
                session?.sendKeepAliveMsg()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                if (session != null) {
                    session?.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                session = null
            }
        }
    }

    private suspend fun execute(command: String) {
        withContext(Dispatchers.IO) {
            try {
                if (session != null && connected) {
                    val channel = session?.openChannel("exec") as ChannelExec
                    channel.setCommand(command)
                    channel.connect()
                    channel.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun displayLogos() {
        val command = "chmod 777 /var/www/html/kml/$logoSlave.kml; echo '${KMLUtils.screenOverlayImage()}' > /var/www/html/kml/$logoSlave.kml"
        execute(command)
    }

    suspend fun clearKml() {
        for (i in 2..screens) {
            execute("chmod 777 /var/www/html/kml/slave_$i.kml; echo '' > /var/www/html/kml/slave_$i.kml")
        }
        execute("echo '' > /tmp/query.txt")
        execute("echo '' > /var/www/html/kmls.txt")
    }

    suspend fun uploadFile(name: String, file: File) {
        withContext(Dispatchers.IO) {
            if (session != null && connected) {
                val channel = session?.openChannel("sftp") as ChannelSftp
                channel.connect()
                try {
                    val remotePath = "/var/www/html/$name.kml"
                    channel.put(file.absolutePath, remotePath)
                    channel.chmod(644, remotePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                    channel.disconnect()
                } finally {
                    channel.disconnect()
                }
            }
        }
    }

    suspend fun deleteFile(name: String) {
        withContext(Dispatchers.IO) {
            if (session != null && connected) {
                val channel = session?.openChannel("sftp") as ChannelSftp
                channel.connect()
                try {
                    channel.rm("/var/www/html/$name")
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    channel.disconnect()
                }
            }
        }
    }

    suspend fun flyTo(cameraPosition: CameraPosition) {
        execute("echo 'flytoview=${KMLUtils.lookAt(cameraPosition)}' > /tmp/query.txt")
    }

    suspend fun setRefresh() {
        for (i in 2..screens) {
            val search = "<href>##LG_PHPIFACE##kml/slave_$i.kml</href>"
            val replace =
                "<href>##LG_PHPIFACE##kml/slave_$i.kml</href><refreshMode>onInterval</refreshMode><refreshInterval>2</refreshInterval>"
            execute("""sshpass -p $password ssh -t lg$i 'echo $password | sudo -S sed -i "s|$replace|$search|" ~/earth/kml/slave/myplaces.kml'""")
            execute("""sshpass -p $password ssh -t lg$i 'echo $password | sudo -S sed -i "s|$search|$replace|" ~/earth/kml/slave/myplaces.kml'""")
        }
    }

    suspend fun resetRefresh() {
        for (i in 2..screens) {
            val search =
                "<href>##LG_PHPIFACE##kml\\/slave_$i.kml<\\/href><refreshMode>onInterval<\\/refreshMode><refreshInterval>2<\\/refreshInterval>"
            val replace = "<href>##LG_PHPIFACE##kml\\/slave_$i.kml<\\/href>"
            execute(
                """sshpass -p $password ssh -t lg$i 'echo $password | sudo -S sed -i "s|${
                    Regex.escape(
                        search
                    )
                }|$replace|" ~/earth/kml/slave/myplaces.kml'"""
            )
        }
    }

    suspend fun relaunch() {
        for (i in 1..screens) {
            val command = """/home/$username/bin/lg-relaunch > /home/$username/log.txt;
                RELAUNCH_CMD="if [ -f /etc/init/lxdm.conf ]; then
                    export SERVICE=lxdm
                elif [ -f /etc/init/lightdm.conf ]; then
                    export SERVICE=lightdm
                else
                    exit 1
                fi
                if  [[ \${'$'}(service \${'$'}SERVICE status) =~ 'stop' ]]; then
                    echo $password | sudo -S service \${'$'}SERVICE start
                else
                    echo $password | sudo -S service \${'$'}SERVICE restart
                fi
                " && sshpass -p $password ssh -x -t lg@lg$i "${'$'}RELAUNCH_CMD"""".trimMargin()

            execute(command)
        }
    }

    suspend fun restart() {
        for (i in 1..screens) {
            execute("""sshpass -p $password ssh -t lg$i "echo $password | sudo -S reboot"""")
        }
    }

    suspend fun shutdown() {
        for (i in 1..screens) {
            execute("""sshpass -p $password ssh -t lg$i "echo $password | sudo -S poweroff"""")
        }
    }

    suspend fun performAction(state: State, direction: String?) {
        if ((state == State.IDLE) || (lastState != State.IDLE && lastState != state)) {
            execute("export DISPLAY=:0; xdotool keyup ${lastState.key}")
            lastState = State.IDLE
        }
        if (state != State.IDLE) {
            val command = when (state) {
                State.PLANET -> "echo 'planet=$direction' > /tmp/query.txt"
                State.FLY_TO -> "echo 'search=$direction' > /tmp/query.txt"
                else -> "export DISPLAY=:0; xdotool keydown ${state.key}"
            }
            execute(command)
            lastState = when (state) {
                State.PLANET, State.FLY_TO -> State.IDLE
                else -> state
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LiquidGalaxyManager? = null

        fun newInstance(
            username: String,
            password: String,
            host: String,
            port: Int,
            screens: Int,
        ) {
            synchronized(this) {
                INSTANCE = LiquidGalaxyManager(
                    username = username,
                    password = password,
                    host = host,
                    port = port,
                    screens = screens,
                ).also {
                    INSTANCE = it
                }
            }
        }

        fun getInstance(): LiquidGalaxyManager? {
            return INSTANCE
        }
    }
}