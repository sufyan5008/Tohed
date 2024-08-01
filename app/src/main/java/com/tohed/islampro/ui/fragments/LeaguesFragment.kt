package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
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
    private val allPosts = mutableListOf<Post>()

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
            handlePostItemClick(post)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = postAdapter
    }

    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle()
        args.putLong("postId", postId)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_leagueFragment_to_postDetailsFragment, args)
    }

    private fun fetchCategories(parentCategoryId: Int) {
        showLoader()
        allCategoryIds.clear() // Clear previous category IDs to prevent duplication
        allPosts.clear() // Clear previous posts to fetch fresh data
        postApiService.getSubcategoriesByParent(parentCategoryId)
            .enqueue(object : Callback<List<Category>> {
                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {
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
                        hideLoader()
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    showError(t.message ?: "Unknown error")
                    hideLoader()
                }
            })
    }

    private fun fetchSubcategoriesRecursively(categoryId: Int) {
        postApiService.getSubcategoriesByParent(categoryId)
            .enqueue(object : Callback<List<Category>> {
                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {
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
                        hideLoader()
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    showError(t.message ?: "Unknown error")
                    hideLoader()
                }
            })
    }

    private fun fetchAllPosts() {
        var pendingRequests = allCategoryIds.size
        for (categoryId in allCategoryIds) {
            postApiService.getPostsByCategory(categoryId, 1).enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val posts = response.body()
                        posts?.let {
                            allPosts.addAll(it)
                        }
                    } else {
                        showError("Failed to fetch posts")
                    }
                    pendingRequests--
                    if (pendingRequests == 0) {
                        updateRecyclerView()
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    showError(t.message ?: "Unknown error")
                    pendingRequests--
                    if (pendingRequests == 0) {
                        updateRecyclerView()
                    }
                }
            })
        }
    }

    private fun updateRecyclerView() {
        postAdapter.updatePosts(allPosts)
        hideLoader()
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE
    }
}
