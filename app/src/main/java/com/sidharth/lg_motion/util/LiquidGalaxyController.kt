package com.sidharth.lg_motion.util

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Properties


class LiquidGalaxyController(
    private val username: String,
    private val password: String,
    private val host: String,
    private val port: Int,
    private val screens: Int,
) {
    enum class State(val key: String) {
        IDLE("idle"), FLY_TO("idle"), PLANET("idle"), MOVE_NORTH("Up"), MOVE_SOUTH("Down"), MOVE_EAST(
            "Right"
        ),
        MOVE_WEST("Left"), ROTATE_LEFT("ctrl+Left"), ROTATE_RIGHT("ctrl+Right"), ZOOM_IN("equal"), ZOOM_OUT(
            "minus"
        ),
    }

    private var lastState = State.IDLE
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

    private suspend fun setupNewSession() {
        val jSch = JSch()
        session = jSch.getSession(username, host, port)
        session?.setPassword(password)
        val config = Properties()
        config["StrictHostKeyChecking"] = "no"
        session?.setConfig(config)
        session?.connect()
    }

    private suspend fun execute(command: String) {
        if (session != null && connected) {
            val channel = session?.openChannel("exec") as ChannelExec
            channel.outputStream = ByteArrayOutputStream()
            channel.setCommand(command)
            channel.connect()
            channel.disconnect()
        }
    }

    suspend fun connect(): Boolean {
        return try {
            if (session == null || connected.not()) {
                setupNewSession()
                displayLogos()
            } else {
                session?.sendKeepAliveMsg()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun disconnect() {
        if (session != null) {
            session?.disconnect()
        }
        session = null
    }

    private suspend fun displayLogos() {
        val command = ""
        execute(command)
    }

    suspend fun hideLogos() {
        val command = """
            chmod 777 /var/www/html/kml/$logoSlaves.kml; echo '
            <kml xmlns="http://www.opengis.net/kml/2.2"
            xmlns:atom="http://www.w3.org/2005/Atom"
            xmlns:gx="http://www.google.com/kml/ext/2.2">
            <Document id="$logoSlaves">
            </Document>
            </kml>
            ' > /var/www/html/kml/$logoSlaves.kml
            """.trimMargin()

        execute(command)
    }


    //    suspend fun cleanSlaves() {
//        for (i in 2..screens) {
//            execute("echo '' > /var/www/html/kml/slave_$i.kml")
//        }
//    }
    suspend fun sendKmlInSlave(slave: Int, kml: String) {
        execute("echo '$kml' > /var/www/html/kml/slave_$slave.kml")
    }

    suspend fun sendKml(name: String) {
        execute("echo '\nhttp://lg1:81/$name.kml' > /var/www/html/kmls.txt")
    }

    suspend fun clearKml() {
        for (i in 2..screens) {
            execute("echo '' > /var/www/html/kml/slave_$i.kml")
        }
        stopOrbit()
        execute("""echo "" > /tmp/query.txt""")
        execute("echo '' > /var/www/html/kmls.txt")
    }

    suspend fun uploadFile(name: String, file: File) {
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

    suspend fun deleteFile(name: String) {
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

    suspend fun startOrbit() {
        execute("""echo "playtour=Orbit" > /tmp/query.txt""")
    }

    suspend fun stopOrbit() {
        execute("""echo "exittour=true" > /tmp/query.txt""")
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
            val command = """RELAUNCH_CMD="\\
                if [ -f /etc/init/lxdm.conf ]; then
                export SERVICE=lxdm
                elif [ -f /etc/init/lightdm.conf ]; then
                export SERVICE=lightdm
                else
                exit 1
                fi
                if  [[ $(service ${'$'}SERVICE status) =~ 'stop' ]]; then
                echo $password | sudo -S service ${'$'}{SERVICE} start
                else
                echo $password | sudo -S service ${'$'}{SERVICE} restart
                fi
                " && sshpass -p $password ssh -x -t lg@lg\$i "${'$'}RELAUNCH_CMD\"""".trimMargin()

            execute(""""/home/$username/bin/lg-relaunch" > /home/$username/log.txt""")
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
        val focusWindow = """xdotool windowfocus $(xdotool search --name "Google Earth Pro")"""
        if (lastState != State.IDLE) {
            execute("""$focusWindow keyup ${lastState.key}""")
        }
        val command = when (state) {
            State.PLANET -> """$focusWindow && echo 'planet=$direction' > /tmp/query.txt`"""
            State.FLY_TO -> """$focusWindow && echo 'search=$direction' > /tmp/query.txt"""
            else -> "$focusWindow keydown ${state.key}"
        }
        execute(command)
        lastState = state
    }

    companion object {
        @Volatile
        private var INSTANCE: LiquidGalaxyController? = null

        fun newInstance(
            username: String,
            password: String,
            host: String,
            port: Int,
            screens: Int,
        ) {
            synchronized(this) {
                INSTANCE = LiquidGalaxyController(
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

        fun getInstance(): LiquidGalaxyController? {
            return INSTANCE
        }
    }
}