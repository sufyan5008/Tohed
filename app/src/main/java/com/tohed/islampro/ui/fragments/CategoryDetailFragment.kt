package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.adapters.CategoryAdapter
import com.tohed.islampro.databinding.FragmentCategoryDetailBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.viewModel.PostViewModel

class CategoryDetailFragment : Fragment() {

    private lateinit var binding: FragmentCategoryDetailBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val postViewModel: PostViewModel by viewModels()
    private var categoryId: Int = -1
    private lateinit var categoryTitle: String
    private var hasLoadedData: Boolean = false
    private var progressDialog: ProgressDialogFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            categoryId = it.getInt("categoryId", -1)
            categoryTitle = it.getString("categoryTitle", "")
            val posts = it.getParcelableArray("posts")?.map { it as Post } ?: emptyList()

            // Set the category title
            binding.root.findViewById<TextView>(R.id.categoryTitleTextView).text = categoryTitle

            setupRecyclerView(posts)

            // Check if data is already loaded
            if (!hasLoadedData && categoryId != -1 && posts.isEmpty()) {
                postViewModel.fetchPostsByCategory(categoryId)
                hasLoadedData = true
            }
        }

        setupObservers()
    }

    private fun setupRecyclerView(posts: List<Post>) {
        categoryAdapter = CategoryAdapter(posts) { post ->
            handlePostItemClick(post)
        }

        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun setupObservers() {
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            if (postViewModel.loadingLiveData.value == true) {
                return@observe
            }
            progressDialog?.dismiss()
            if (posts.isNullOrEmpty()) {
                showErrorMessage("No posts found")
            } else {
                showPosts(posts)
            }
        }

        postViewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            progressDialog?.dismiss()
            showErrorMessage(error)
        }

        postViewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressDialog = ProgressDialogFragment()
                progressDialog?.show(childFragmentManager, "loadingDialog")
            } else {
                progressDialog?.dismiss()
            }
        }
    }

    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle().apply {
            putLong("postId", postId)
        }
        findNavController().navigate(R.id.action_categoryDetailFragment_to_postDetailsFragment, args)
    }

    private fun showPosts(posts: List<Post>) {
        categoryAdapter.updatePosts(posts)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}


/*
package com.tohed.islampro.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        // Call your API service to fetch posts based on the categoryId
        apiService.getPostsByCategory(categoryId, page).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {

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
                // Handle failure case
                showErrorMessage("Network request failed: ${t.message}")
            }
        })
    }

    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle().apply {
            putLong("postId", postId)
        }
        findNavController().navigate(R.id.action_categoryDetailFragment_to_postDetailsFragment, args)
    }

    private fun showPosts(posts: List<Post>) {
        categoryAdapter.updatePosts(posts)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}*/
