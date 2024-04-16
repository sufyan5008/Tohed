package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.datamodel.Category

/*
class CategoryAdapter(private var categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.textCategoryName)

       */
/* fun bind(category: Category) {
            categoryNameTextView.text = category.name
        }*//*

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    */
/*override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }*//*


    override fun getItemCount(): Int {
        return categories.size
    }

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
*/


/*class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.MatchView>() {

    inner class MatchView(val binding: FavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchView {
        val binding =
            FavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchView(binding);
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MatchView, position: Int) {

    }
}*/