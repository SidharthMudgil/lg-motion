package com.sidharth.lg_motion.ui.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sidharth.lg_motion.databinding.ItemCardFunActivityBinding
import com.sidharth.lg_motion.domain.callback.OnFunActivityClickCallback
import com.sidharth.lg_motion.domain.model.FunActivity
import com.sidharth.lg_motion.util.ImageColorExtractor

class FunActivitiesAdapter(
    private val context: Context,
    private val activities: List<FunActivity>,
    private val onFunActivityClickCallback: OnFunActivityClickCallback,
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
        holder.itemBinding.cover.setOnClickListener {
            onFunActivityClickCallback.onFunActivityClick(activities[position].name)
        }
        holder.bind(context, activities[position])
    }

    class FunActivityViewHolder(
        val itemBinding: ItemCardFunActivityBinding
    ) : ViewHolder(itemBinding.root) {
        fun bind(context: Context, activity: FunActivity) {
            itemBinding.cover.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    activity.cover
                )
            )
            val bitmap = BitmapFactory.decodeResource(context.resources, activity.cover)
            ImageColorExtractor.extractColorsAsync(
                bitmap, object : ImageColorExtractor.ColorExtractionCallback {
                    override fun onColorsExtracted(colors: ImageColorExtractor.Colors) {
                        itemBinding.cover.background = ColorDrawable(colors.mutedLight)
                    }
                })
        }
    }
}