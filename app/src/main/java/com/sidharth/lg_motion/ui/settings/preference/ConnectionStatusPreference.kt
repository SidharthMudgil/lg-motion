package com.sidharth.lg_motion.ui.settings.preference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.android.material.card.MaterialCardView
import com.sidharth.lg_motion.R

class ConnectionStatusPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    init {
        isSelectable = false
    }

    private var isConnected: Boolean = false

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val connectedCard = holder.itemView.findViewById<MaterialCardView>(R.id.connected_card)
        val disconnectedCard = holder.itemView.findViewById<MaterialCardView>(R.id.disconnected_card)

        if (isConnected) {
            connectedCard.visibility = View.VISIBLE
            disconnectedCard.visibility = View.GONE
        } else {
            connectedCard.visibility = View.GONE
            disconnectedCard.visibility = View.VISIBLE
        }
    }

    fun getConnectionStatus(): Boolean {
        return this.isConnected
    }

    fun setConnectionStatus(isConnected: Boolean) {
        this.isConnected = isConnected
        persistBoolean(isConnected)
        notifyChanged()
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getBoolean(index, false)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)
        setConnectionStatus(getPersistedBoolean(defaultValue as? Boolean ?: false))
    }
}
