package com.tohed.islampro.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.adapters.CategoryAdapter
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.databinding.FragmentCategoryDetailBinding
import com.tohed.islampro.datamodel.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryDetailFragment : Fragment() {

    private lateinit var binding: FragmentCategoryDetailBinding
    private lateinit var apiService: PostApiService
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var progressDialog: ProgressDialog
    private var categoryId: Int = -1 // Default value, update it with the actual category ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            categoryId = it.getInt("categoryId")
            val posts = it.getParcelableArray("posts")?.map { it as Post } ?: emptyList()
            setupRecyclerView(posts)
        }
        apiService = PostApiService.getService()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading posts...")
        progressDialog.setCancelable(false)

        if (categoryAdapter.itemCount == 0) {
            fetchPosts(categoryId, 1)
        }
    }

    private fun setupRecyclerView(posts: List<Post>) {
        categoryAdapter = CategoryAdapter(posts) { post ->
            // Handle item click here
            handlePostItemClick(post)
        }

        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun fetchPosts(categoryId: Int, page: Int) {
        progressDialog.show() // Show the progress dialog

        // Call your API service to fetch posts based on the categoryId
        apiService.getPostsByCategory(categoryId, page).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                progressDialog.dismiss() // Hide the progress dialog

                if (response.isSuccessful) {
                    val posts = response.body()
                    // Update your UI with the fetched posts
                    if (posts != null) {
                        showPosts(posts)
                    } else {
                        showErrorMessage("No posts found")
                    }
                } else {
                    // Handle unsuccessful response
                    showErrorMessage("Failed to fetch posts: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                progressDialog.dismiss() // Hide the progress dialog
                // Handle failure case
                showErrorMessage("Network request failed: ${t.message}")
            }
        })
    }

    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toInt()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Int) {
        val args = Bundle()
        args.putInt("postId", postId)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_categoryDetailFragment_to_postDetailsFragment, args)
    }

    private fun showPosts(posts: List<Post>) {
        categoryAdapter.updatePosts(posts)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
