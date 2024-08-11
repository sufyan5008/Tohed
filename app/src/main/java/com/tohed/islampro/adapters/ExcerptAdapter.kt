package com.tohed.islampro.adapters

import android.graphics.Typeface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.datamodel.Post

class ExcerptAdapter(
    private val posts: List<Post>,
    private val onItemClick: (Post) -> Unit // Click listener to handle item clicks
) : RecyclerView.Adapter<ExcerptAdapter.ExcerptViewHolder>() {

    inner class ExcerptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val excerptTextView: TextView = itemView.findViewById(R.id.excerptTextView)
        private val cardView: CardView = itemView as CardView // Cast itemView to CardView

        fun bind(post: Post) {
            titleTextView.text = post.title.rendered

            // Apply the custom font to the excerptTextView
            val typeface = Typeface.createFromAsset(itemView.context.assets, "fonts/mehr_urdu_font.ttf")
            excerptTextView.typeface = typeface
            excerptTextView.text = Html.fromHtml(post.excerpt.rendered)

            // Make the entire CardView clickable
            cardView.setOnClickListener { onItemClick(post) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcerptViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_excerpt, parent, false)
        return ExcerptViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExcerptViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size
}
