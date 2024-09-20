package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.datamodel.Page

class HomeRecyclerViewAdapter(private var pages: List<Page>) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.PageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    // Add a method to update the data
    fun updateData(newPages: List<Page>) {
        pages = newPages
        notifyDataSetChanged()  // Notify adapter to rebind the data
    }

    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private val postTitle: TextView = view.findViewById(R.id.postHTitleTextView)
        //private val postExcerpt: TextView = view.findViewById(R.id.postExcerptTextView)
        private val postContent: TextView = view.findViewById(R.id.postContentTextView)

        fun bind(page: Page) {
           // postTitle.text = page.title.rendered
           // postExcerpt.text = page.content.rendered  // Use excerpt or another appropriate field
            postContent.text = page.content.html  // Or another field depending on how the data is structured
        }
    }
}
