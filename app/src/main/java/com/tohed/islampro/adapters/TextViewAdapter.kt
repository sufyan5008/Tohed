package com.tohed.islampro.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohed.OnItemClickListener
import com.tohed.islampro.databinding.ItemTextViewBinding

class TextViewAdapter(
    private val context: Context,
    private val texts: Array<String>,
    private val backgrounds: Array<Int>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TextViewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTextViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String, background: Int) {
            binding.textView.text = text
            binding.root.setBackgroundResource(background)
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(texts[position], backgrounds[position % backgrounds.size])
    }

    override fun getItemCount(): Int {
        return texts.size
    }
}
