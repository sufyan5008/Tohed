package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tohed.islampro.R
import com.tohed.islampro.databinding.FragmentCategoryBinding
import com.tohed.islampro.viewModel.PostViewModel

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val postViewModel: PostViewModel by viewModels()
    private var progressDialog: ProgressDialogFragment? = null
    private var hasNavigatedToDetail: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        hasNavigatedToDetail = false

        // Ensure the progress dialog is dismissed when coming back to this fragment
        progressDialog?.dismiss()

        // Reset loadingLiveData to avoid showing progress dialog again unnecessarily
        //postViewModel.loadingLiveData.value = false
    }


    private fun setupClickListeners() {
        binding.masnoonNimaz.setOnClickListener { onCategoryClick(409, "مسنون نماز") }
        binding.tohedAqaed.setOnClickListener { onCategoryClick(407, "توحید و عقائد") }
        binding.qabar.setOnClickListener { onCategoryClick(456, "قبروں سے متعلق") }
        binding.khwatin.setOnClickListener { onCategoryClick(414, "گوشہ خواتین") }
        binding.itbaaSunnat.setOnClickListener { onCategoryClick(410, "اتباع سنت و ترک بدعات") }
        binding.ahkamMasail.setOnClickListener { onCategoryClick(413, "احکام ومسائل") }
        binding.husnEIkhlaq.setOnClickListener { onCategoryClick(412, "حسن اخلاق معاشرت") }
        binding.zakatSadqat.setOnClickListener { onCategoryClick(431, "زکوة و صدقات") }
        binding.hajjUmrah.setOnClickListener { onCategoryClick(416, "حج و عمرہ") }
        binding.rozonAhkam.setOnClickListener { onCategoryClick(415, "روزوں کے احکام") }
    }

    private fun onCategoryClick(categoryId: Int, categoryTitle: String) {
        postViewModel.currentCategoryTitle = categoryTitle

        if (postViewModel.loadingLiveData.value == true) {
            return  // Prevent showing the progress dialog again if already loading.
        }

        //showProgressDialog()

        // Fetch posts by the selected category ID
        postViewModel.fetchPostsByCategory(categoryId)
    }

    private fun setupObservers() {
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            if (postViewModel.loadingLiveData.value == true || hasNavigatedToDetail) {
                return@observe
            }

            progressDialog?.dismiss()

            if (posts.isNullOrEmpty()) {
                Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putParcelableArray("posts", posts.toTypedArray())
                    putString("categoryTitle", postViewModel.currentCategoryTitle)
                }
                hasNavigatedToDetail = true
                findNavController().navigate(
                    R.id.action_categoryFragment_to_categoryDetailFragment, bundle
                )
            }
        }

        postViewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            Log.d("CategoryFragment", "Loading state changed: $isLoading")
            if (isLoading && !hasNavigatedToDetail) {
                showProgressDialog()
            } else {
                progressDialog?.dismiss()
            }
        }

        postViewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            progressDialog?.dismiss()
            Toast . makeText (context, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressDialog() {
        if (progressDialog == null || progressDialog?.isAdded == false) {
            progressDialog = ProgressDialogFragment()
            progressDialog?.show(childFragmentManager, "loadingDialog")
        }
    }
}


/*
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

            fetchPostsByCategory(456)
        }
        binding.khwatin.setOnClickListener {
            progressDialog.show()

            fetchSubcategoriesAndPosts(414)
        }
        binding.itbaaSunnat.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(410)
        }
        binding.ahkamMasail.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(413)
        }
        binding.husnEIkhlaq.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(412)
        }
        binding.zakatSadqat.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(431)
        }
        binding.hajjUmrah.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(416)
        }
        binding.rozonAhkam.setOnClickListener {
            progressDialog.show()

            fetchPostsByCategory(415)
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
*/
