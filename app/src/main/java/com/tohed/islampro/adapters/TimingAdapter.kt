package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.databinding.ToDatesViewBinding

class TimingAdapter : RecyclerView.Adapter<TimingAdapter.MatchView>() {

    inner class MatchView(val binding: ToDatesViewBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchView {
        val binding =
            ToDatesViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchView(binding);
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: MatchView, position: Int) {

    }
}