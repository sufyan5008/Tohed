package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.datamodel.Post
import java.text.SimpleDateFormat
import java.util.Locale
import com.tohed.islampro.R
import java.text.ParseException

class PostAdapter(private var posts: List<Post>, private val onItemClickListener: (Post) -> Unit) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()) // Adjust format as needed

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postTitleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
        private val postDateTextView: TextView = itemView.findViewById(R.id.dateTextView)


        fun bind(post: Post) {
            postTitleTextView.text = post.title.rendered
            postDateTextView.text = formatDate(post.date)

            itemView.setOnClickListener { onItemClickListener(post) }
        }

        private fun formatDate(date: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            return outputFormat.format(parsedDate!!)
        }
    }
    private fun parseDate(dateString: String): Long? {
        return try {
            dateFormat.parse(dateString)?.time
        } catch (e: ParseException) {
            null
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

    /*fun updatePosts(newPosts: List<Post>) {
        val diffCallback = PostDiffCallback(posts, newPosts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        posts = newPosts
        diffResult.dispatchUpdatesTo(this)
    }*/
    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts.sortedByDescending { parseDate(it.date) ?: Long.MIN_VALUE }
        notifyDataSetChanged()
    }

    fun clearPosts() {
        posts = emptyList()
        notifyDataSetChanged()
    }

    class PostDiffCallback(
        private val oldList: List<Post>,
        private val newList: List<Post>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}



/*
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
*/
