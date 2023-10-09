package com.sidharth.lg_motion.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.sidesheet.SideSheetDialog
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.databinding.ConnectionDialogBinding

object DialogUtils {

    @SuppressLint("StaticFieldLeak")
    private var bottomSheetDialog: BottomSheetDialog? = null
    @SuppressLint("StaticFieldLeak")
    private var sideSheetDialog: SideSheetDialog? = null

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

        if (context.isTablet() || !context.isPortrait())
            sideSheetDialog = SideSheetDialog(context)
        else
            bottomSheetDialog = BottomSheetDialog(context)

        binding.username.setText(username)
        binding.password.setText(password)
        binding.ip.setText(ip)
        binding.port.setText(port)
        binding.totalScreens.value = screens.toFloat()

        binding.port.filters = arrayOf(RangeInputFilter(max = 65536))

        binding.btnConnect.setOnClickListener {
            (if (context.isTablet() || !context.isPortrait()) sideSheetDialog else bottomSheetDialog)?.apply {
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
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            (if (context.isTablet() || !context.isPortrait()) sideSheetDialog else bottomSheetDialog)?.dismiss()
        }

        if (context.isTablet() || !context.isPortrait()) {
            sideSheetDialog?.apply {
                setContentView(view)
                behavior.isDraggable = false
                create()
            }
        } else {
            bottomSheetDialog?.apply {
                setContentView(view)
                behavior.state = STATE_EXPANDED
                create()
            }
        }
    }

    fun show(context: Context, callback: () -> Unit) {
        if (context.isTablet() || !context.isPortrait()) {
            if (sideSheetDialog == null) {
                init(context, callback)
            }
            sideSheetDialog?.show()
        } else {
            if (bottomSheetDialog == null) {
                init(context, callback)
            }
            bottomSheetDialog?.show()
        }
    }

    fun dismiss(context: Context) {
        if (context.isTablet() || !context.isPortrait()) {
            if (sideSheetDialog != null && sideSheetDialog?.isShowing == true) {
                sideSheetDialog?.dismiss()
            }

            sideSheetDialog = null
        } else {
            if (bottomSheetDialog != null && bottomSheetDialog?.isShowing == true) {
                bottomSheetDialog?.dismiss()
            }

            bottomSheetDialog = null
        }
    }

    private fun Context.isTablet(): Boolean {
        return resources.getBoolean(R.bool.tablet)
    }

    private fun Context.isPortrait(): Boolean {
        return resources.getBoolean(R.bool.portrait)
    }

}