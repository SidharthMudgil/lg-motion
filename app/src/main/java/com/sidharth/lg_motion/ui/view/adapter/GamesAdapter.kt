package com.sidharth.lg_motion.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sidharth.lg_motion.databinding.ItemCardGameBinding
import com.sidharth.lg_motion.domain.model.Feature

class GamesAdapter(
    private val context: Context,
    private val games: List<Feature>,
) : Adapter<GamesAdapter.GamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val binding = ItemCardGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GamesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        holder.bind(context, games[position])
    }

    class GamesViewHolder(
        private val itemBinding: ItemCardGameBinding
    ) : ViewHolder(itemBinding.root) {
        fun bind(context: Context, feature: Feature) {
            itemBinding.cover.setImageDrawable(ContextCompat.getDrawable(context, feature.cover))
            itemBinding.cover.setOnClickListener {

            }
        }
    }
}