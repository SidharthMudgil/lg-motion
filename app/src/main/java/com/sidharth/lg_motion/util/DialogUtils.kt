package com.sidharth.lg_motion.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.databinding.ConnectionDialogBinding

object DialogUtils {
    private var dialog: Dialog? = null

    private fun init(context: Context, callback: () -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.connection_dialog, null)
        val binding = ConnectionDialogBinding.bind(view)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        var username = preferences.getString("username", "") ?: ""
        var password = preferences.getString("password", "") ?: ""
        var ip = preferences.getString("ip", "") ?: ""
        var port = preferences.getString("port", "") ?: ""
        var screens = preferences.getInt("screens", 3)

        binding.username.setText(username)
        binding.password.setText(password)
        binding.ip.setText(ip)
        binding.port.setText(port)
        binding.totalScreens.value = screens.toFloat()

        binding.port.filters = arrayOf(RangeInputFilter(max = 65536))

        dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle(context.getString(R.string.connection_dialog_title))
            .setMessage(context.getString(R.string.connection_dialog_message))
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Connect") { dialog, _ ->
                username = binding.username.text.toString()
                password = binding.password.text.toString()
                ip = binding.ip.text.toString()
                port = binding.port.text.toString()
                screens = binding.totalScreens.value.toInt()

                if (username.isNotBlank() && password.isNotBlank() && TextUtils.isValidIp(ip) && port.isNotBlank()) {
                    editor.putString("username", username).apply()
                    editor.putString("password", password).apply()
                    editor.putString("ip", ip).apply()
                    editor.putString("port", port).apply()
                    editor.putInt("screens", screens).apply()

                    LiquidGalaxyManager.newInstance(
                        username = username,
                        password = password,
                        host = ip,
                        port = port.toInt(),
                        screens = screens
                    )

                    callback()
                }
                dialog.dismiss()
            }.create()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    fun show(context: Context, callback: () -> Unit) {
        if (dialog == null) {
            init(context, callback)
        }
        dialog?.show()
    }

    fun dismiss() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }
}