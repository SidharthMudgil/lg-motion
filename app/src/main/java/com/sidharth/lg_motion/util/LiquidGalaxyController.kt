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
        IDLE("idle"),
        FLY_TO("idle"),
        MOVE_NORTH("Up"),
        MOVE_SOUTH("Down"),
        MOVE_EAST("Right"),
        MOVE_WEST("Left"),
        ROTATE_LEFT("ctrl+Left"),
        ROTATE_RIGHT("ctrl+Right"),
        ZOOM_IN("equal"),
        ZOOM_OUT("minus")
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

    private fun execute(command: String) {
        if (session != null && connected) {
            val channel = session?.openChannel("exec") as ChannelExec
            channel.outputStream = ByteArrayOutputStream()
            channel.setCommand(command)
            channel.connect()
            channel.disconnect()
        }
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

    private fun displayLogos() {
        val command = ""
        execute(command)
    }

    fun hideLogos() {
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


    //    fun cleanSlaves() {
//        for (i in 2..screens) {
//            execute("echo '' > /var/www/html/kml/slave_$i.kml")
//        }
//    }
    fun sendKmlInSlave(slave: Int, kml: String) {
        execute("echo '$kml' > /var/www/html/kml/slave_$slave.kml")
    }

    fun sendKml(name: String) {
        execute("echo '\nhttp://lg1:81/$name.kml' > /var/www/html/kmls.txt")
    }

    fun clearKml() {
        for (i in 2..screens) {
            execute("echo '' > /var/www/html/kml/slave_$i.kml")
        }
        stopOrbit()
        execute("""echo "" > /tmp/query.txt""")
        execute("echo '' > /var/www/html/kmls.txt")
    }

    fun uploadFile(name: String, file: File) {
        if (session != null && connected) {
            val channel = session?.openChannel("sftp") as ChannelSftp
            channel.outputStream = ByteArrayOutputStream()
            // TODO(main logic left)
            channel.connect()
            channel.disconnect()
        }
    }

    fun deleteFile(name: String) {
        if (session != null && connected) {
            val channel = session?.openChannel("sftp") as ChannelSftp
            channel.outputStream = ByteArrayOutputStream()
            channel.rm("/var/www/html/$name")
            channel.connect()
            channel.disconnect()
        }
    }

    fun startOrbit() {
        execute("""echo "playtour=Orbit" > /tmp/query.txt""")
    }

    fun stopOrbit() {
        execute("""echo "exittour=true" > /tmp/query.txt""")
    }

    fun setRefresh() {
        for (i in 2..screens) {
            val search = "<href>##LG_PHPIFACE##kml/slave_$i.kml</href>"
            val replace =
                "<href>##LG_PHPIFACE##kml/slave_$i.kml</href><refreshMode>onInterval</refreshMode><refreshInterval>2</refreshInterval>"
            execute("""sshpass -p $password ssh -t lg$i 'echo $password | sudo -S sed -i "s|$replace|$search|" ~/earth/kml/slave/myplaces.kml'""")
            execute("""sshpass -p $password ssh -t lg$i 'echo $password | sudo -S sed -i "s|$search|$replace|" ~/earth/kml/slave/myplaces.kml'""")
        }
    }

    fun resetRefresh() {
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

    fun relaunch() {
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

    fun restart() {
        for (i in 1..screens) {
            execute("""sshpass -p $password ssh -t lg$i "echo $password | sudo -S reboot"""")
        }
    }

    fun shutdown() {
        for (i in 1..screens) {
            execute("""sshpass -p $password ssh -t lg$i "echo $password | sudo -S poweroff"""")
        }
    }

    fun performAction(state: State, direction: String?) {
        val focusWindow = """xdotool windowfocus $(xdotool search --name "Google Earth Pro")"""
        if (lastState != State.IDLE) {
            execute("""$focusWindow keyup ${lastState.key}""")
        }
        val command = when (state) {
            State.FLY_TO -> """$focusWindow && echo 'search=$direction' > /tmp/query.txt"""
            else -> "$focusWindow keydown ${state.key}"
        }
        execute(command)
        lastState = state
    }
}