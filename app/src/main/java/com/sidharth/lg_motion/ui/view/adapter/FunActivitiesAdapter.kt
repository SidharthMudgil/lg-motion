package com.sidharth.lg_motion.ui.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sidharth.lg_motion.databinding.ItemCardFunActivityBinding
import com.sidharth.lg_motion.domain.model.FunActivity

class FunActivitiesAdapter(
    private val context: Context,
    private val activities: List<FunActivity>,
) : Adapter<FunActivitiesAdapter.FunActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunActivityViewHolder {
        val binding = ItemCardFunActivityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FunActivityViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    override fun onBindViewHolder(holder: FunActivityViewHolder, position: Int) {
        holder.bind(context, activities[position])
    }

    class FunActivityViewHolder(
        private val itemBinding: ItemCardFunActivityBinding
    ) : ViewHolder(itemBinding.root) {
        fun bind(context: Context, activity: FunActivity) {
            itemBinding.cover.setImageDrawable(ContextCompat.getDrawable(context, activity.cover))
            itemBinding.cover.setOnClickListener {
                Log.d("clicked", activity.name)
            }
        }
    }
}