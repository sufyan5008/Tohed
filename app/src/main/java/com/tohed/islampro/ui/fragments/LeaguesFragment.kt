// LeaguesFragment.kt
package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.databinding.FragmentLeaguesBinding
import com.tohed.islampro.datamodel.Category
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.adapters.PostAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaguesFragment : Fragment() {

    private lateinit var binding: FragmentLeaguesBinding
    private lateinit var postApiService: PostApiService
    private lateinit var postAdapter: PostAdapter
    private val allCategoryIds = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postApiService = PostApiService.getService()
        setupRecyclerView()
        fetchCategories(425) // Replace 425 with the actual parent category ID if different
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList()) { post ->
            // Handle item click if needed
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = postAdapter
    }

    private fun fetchCategories(parentCategoryId: Int) {
        postApiService.getSubcategoriesByParent(parentCategoryId).enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    categories?.let {
                        for (category in it) {
                            allCategoryIds.add(category.id)
                            fetchSubcategoriesRecursively(category.id)
                        }
                    }
                } else {
                    showError("Failed to fetch categories")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                showError(t.message ?: "Unknown error")
            }
        })
    }

    private fun fetchSubcategoriesRecursively(categoryId: Int) {
        postApiService.getSubcategoriesByParent(categoryId).enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val subcategories = response.body()
                    subcategories?.let {
                        for (subcategory in it) {
                            allCategoryIds.add(subcategory.id)
                            fetchSubcategoriesRecursively(subcategory.id)
                        }
                    }
                    // After fetching all subcategories, fetch posts
                    fetchAllPosts()
                } else {
                    showError("Failed to fetch subcategories")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                showError(t.message ?: "Unknown error")
            }
        })
    }

    private fun fetchAllPosts() {
        val allPosts = mutableListOf<Post>()
        for (categoryId in allCategoryIds) {
            postApiService.getPostsByCategory(categoryId, 1).enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val posts = response.body()
                        posts?.let {
                            allPosts.addAll(it)
                        }
                        // Update the RecyclerView with all posts
                        postAdapter.updatePosts(allPosts)
                    } else {
                        showError("Failed to fetch posts")
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    showError(t.message ?: "Unknown error")
                }
            })
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
