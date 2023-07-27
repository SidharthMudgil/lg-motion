package com.sidharth.lg_motion.ui.home.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sidharth.lg_motion.databinding.ItemCardFeatureBinding
import com.sidharth.lg_motion.domain.callback.OnFeatureClickCallback
import com.sidharth.lg_motion.domain.model.Feature
import com.sidharth.lg_motion.util.ImageColorExtractor

class FeaturesListAdapter(
    private val context: Context,
    private val features: List<Feature>,
    private val onFeatureClickCallback: OnFeatureClickCallback
) : Adapter<FeaturesListAdapter.FeatureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val binding = ItemCardFeatureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FeatureViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return features.size
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        holder.bind(context, features[position])
        holder.itemBinding.featureCard.setOnClickListener {
            onFeatureClickCallback.onFeatureClick(features[position].type)
        }
    }

    class FeatureViewHolder(
        val itemBinding: ItemCardFeatureBinding
    ) : ViewHolder(itemBinding.root) {
        fun bind(context: Context, feature: Feature) {
            val drawable = ContextCompat.getDrawable(context, feature.cover)
            itemBinding.cover.setImageDrawable(drawable)
            itemBinding.title.text = feature.title
            itemBinding.description.text = feature.description

            val bitmap = BitmapFactory.decodeResource(context.resources, feature.cover)
            ImageColorExtractor.extractColorsAsync(
                bitmap, object : ImageColorExtractor.ColorExtractionCallback {
                    override fun onColorsExtracted(colors: ImageColorExtractor.Colors) {
                        itemBinding.cover.background = ColorDrawable(colors.mutedLight)
                    }
                })
        }
    }
}