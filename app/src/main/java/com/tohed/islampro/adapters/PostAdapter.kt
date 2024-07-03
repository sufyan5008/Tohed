package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.datamodel.Post
import java.text.SimpleDateFormat
import java.util.Locale
import com.tohed.islampro.R



class PostAdapter(private var posts: List<Post>, private val onItemClickListener: (Post) -> Unit) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postTitleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
        private val postDateTextView: TextView = itemView.findViewById(R.id.dateTextView) // Add date TextView


        fun bind(post: Post) {
            postTitleTextView.text = post.title.rendered
            postDateTextView.text = formatDate(post.date)

            itemView.setOnClickListener { onItemClickListener(post) }
        }

        private fun formatDate(date: String): String {
            // Implement your date formatting logic here
            // Example: Convert date string to a formatted date
            // For example, if the date is in "yyyy-MM-dd'T'HH:mm:ss" format:
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            return outputFormat.format(parsedDate!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_match_view, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    fun clearPosts() {
        posts = emptyList()
        notifyDataSetChanged()
    }
}
