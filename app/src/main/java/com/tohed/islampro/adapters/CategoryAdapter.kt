package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.databinding.UpdateViewBinding
import com.tohed.islampro.datamodel.Post

class CategoryAdapter(
    private var posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.MatchView>() {

    inner class MatchView(val binding: UpdateViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(posts[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchView {
        val binding = UpdateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchView(binding)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MatchView, position: Int) {
        val post = posts[position]
        holder.binding.tvTitle.text = post.title.rendered
    }
}
