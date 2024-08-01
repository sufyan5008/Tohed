package com.tohed.islampro.ui.fragments

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tohed.islampro.R
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.databinding.FragmentCategoryBinding
import com.tohed.islampro.datamodel.Category
import com.tohed.islampro.datamodel.Post
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var apiService: PostApiService
    private lateinit var progressDialog: ProgressDialog
    private val preFetchedPosts = mutableMapOf<Int, List<Post>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        apiService = PostApiService.getService()

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...") // Set the message for the progress dialog
        progressDialog.setCancelable(false) // Make it not cancelable

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.masnoonNimaz.setOnClickListener {
            progressDialog.show()
           // binding.masnoonNimaz.setBackgroundColor(Color.GRAY)
            fetchSubcategoriesAndPosts(409)
        }
        binding.tohedAqaed.setOnClickListener {
            progressDialog.show()

            fetchSubcategoriesAndPosts(407)
        }
        binding.qabar.setOnClickListener {
            progressDialog.show()

            fetchSubcategoriesAndPosts(456)
        }
        binding.khwatin.setOnClickListener {
            progressDialog.show()

            fetchSubcategoriesAndPosts(414)
        }
        binding.waseela.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(408)
        }
        binding.rafeYaden.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(460)
        }
        binding.taqled.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(449)
        }
        binding.drood.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(418)
        }
        binding.ahlJahlyat.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(419)
        }
        binding.gherSabitM.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(411)
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCategoryDetail(categoryId: Int, posts: List<Post>) {
        progressDialog.dismiss()
        val bundle = bundleOf("categoryId" to categoryId, "posts" to posts.toTypedArray())
        navController.navigate(R.id.action_categoryFragment_to_categoryDetailFragment, bundle)
    }

    private fun fetchPostsByCategory(categoryId: Int) {
        apiService.getPostsByCategory(categoryId, 1).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val posts = response.body()
                    posts?.let {
                        navigateToCategoryDetail(categoryId, it)
                    }
                } else {
                    showErrorMessage("Failed to fetch posts: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                showErrorMessage("Network request failed: ${t.message}")
            }
        })
    }

    private fun fetchSubcategoriesAndPosts(parentCategoryId: Int) {
        apiService.getSubcategoriesByParent(parentCategoryId).enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val subcategories = response.body()
                    if (subcategories.isNullOrEmpty()) {
                        showErrorMessage("No subcategories found")
                        return
                    }
                    fetchAllPostsFromSubcategories(parentCategoryId, subcategories)
                } else {
                    showErrorMessage("Failed to fetch subcategories: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                showErrorMessage("Network request failed: ${t.message}")
            }
        })
    }

    private fun fetchAllPostsFromSubcategories(parentCategoryId: Int, subcategories: List<Category>) {
        lifecycleScope.launch {
            val deferredPosts = subcategories.map { category ->
                async(Dispatchers.IO) { fetchAllPostsForCategory(category.id, 1) }
            }
            val parentPostsDeferred = async(Dispatchers.IO) { fetchAllPostsForCategory(parentCategoryId, 1) }
            val allPosts = deferredPosts.awaitAll().flatten() + parentPostsDeferred.await()

            // Sort posts by date (latest first)
            val sortedPosts = allPosts.sortedByDescending { parseDate(it.date) }

            if (sortedPosts.isEmpty()) {
                showErrorMessage("No posts found in subcategories")
            } else {
                navigateToCategoryDetail(parentCategoryId, sortedPosts)
                fetchRemainingPosts(parentCategoryId, subcategories)
            }
        }
    }

    private suspend fun fetchAllPostsForCategory(categoryId: Int, page: Int): List<Post> {
        val allPosts = mutableListOf<Post>()
        var hasMorePosts: Boolean
        val response = try {
            withContext(Dispatchers.IO) {
                apiService.getPostsByCategory(categoryId, page).execute()
            }
        } catch (e: Exception) {
            showErrorMessage("Failed to fetch posts for category $categoryId: ${e.message}")
            return emptyList()
        }
        if (response.isSuccessful) {
            val posts = response.body()
            if (!posts.isNullOrEmpty()) {
                allPosts.addAll(posts)
                hasMorePosts = true
            } else {
                hasMorePosts = false
            }
        } else {
            hasMorePosts = false
        }
        return allPosts
    }

    private fun fetchRemainingPosts(parentCategoryId: Int, subcategories: List<Category>) {
        lifecycleScope.launch {
            val deferredPosts = subcategories.map { category ->
                async(Dispatchers.IO) { fetchAllPostsForCategory(category.id, 2) }
            }
            val parentPostsDeferred = async(Dispatchers.IO) { fetchAllPostsForCategory(parentCategoryId, 2) }
            val allPosts = deferredPosts.awaitAll().flatten() + parentPostsDeferred.await()

            // Sort posts by date (latest first)
            val sortedPosts = allPosts.sortedByDescending { parseDate(it.date) }

            // Handle further use of fetched posts if necessary
        }
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.parse(dateString) ?: Date(0)
    }
}
