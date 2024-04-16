package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.databinding.UpdateViewBinding

class UpdatesAdapter : RecyclerView.Adapter<UpdatesAdapter.MatchView>() {

    inner class MatchView(val binding: UpdateViewBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchView {
        val binding =
            UpdateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchView(binding);
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: MatchView, position: Int) {
        /*holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(LeagueViewActivity.getIntent(holder.itemView.context))
        }*/
    }
}