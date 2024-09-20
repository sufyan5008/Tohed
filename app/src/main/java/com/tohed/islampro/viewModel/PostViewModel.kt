package com.tohed.islampro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchResultsLiveData = MutableLiveData<List<Post>>()
    val searchResultsLiveData: LiveData<List<Post>> get() = _searchResultsLiveData


    private val postRepository = PostRepository(application)
    var currentCategoryTitle: String? = null

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>>
        get() = _postsLiveData

    private val _postDetailsLiveData = MutableLiveData<Post>()
    val postDetailsLiveData: LiveData<Post>
        get() = _postDetailsLiveData

    private var currentPage = 1
    private var isLoading = false

    var currentCategoryId: Int? = null
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    fun fetchPosts() {
        if (isLoading) return  // Prevent multiple simultaneous loads
        isLoading = true
        _loadingLiveData.value = true

        viewModelScope.launch {
            try {
                // Fetch new posts from repository
                val newPosts = postRepository.getPosts(currentPage)

                // Get the current list of posts from LiveData
                val currentPosts = _postsLiveData.value.orEmpty()

                // Append new posts and sort by date (latest first)
                val updatedPosts = (currentPosts + newPosts).distinctBy { it.id }  // Avoid duplicates
                    .sortedByDescending { post -> post.date }  // Sort by date in descending order

                // Update LiveData with sorted posts
                _postsLiveData.postValue(updatedPosts)

                currentPage++  // Increment the page for future fetches
            } catch (e: Exception) {
                _errorLiveData.postValue("Error fetching posts: ${e.message}")
            } finally {
                isLoading = false
                _loadingLiveData.value = false  // Hide loading indicator
            }
        }
    }
    /*fun fetchPosts() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val posts = postRepository.getPosts(currentPage)
            val currentPosts = _postsLiveData.value ?: emptyList()
            _postsLiveData.postValue(posts)
            currentPage++
            isLoading = false
        }
    }*/


    /*fun fetchPostsByCategory(categoryId: Int) {
        if (isLoading) return
        isLoading = true


        // Reset current category and pagination if a new category is selected
        if (currentCategoryId != categoryId) {
            currentCategoryId = categoryId
            currentPage = 1
            _postsLiveData.value = emptyList() // Clear the LiveData to reset the UI
        }
        _loadingLiveData.value = true // Set loading state to true when starting

        viewModelScope.launch {
            try {
                val posts = postRepository.getPostsByCategory(categoryId, currentPage)
                val updatedPosts = (_postsLiveData.value ?: emptyList()) + posts
                _postsLiveData.postValue(updatedPosts)
                currentPage++
            } catch (e: Exception) {
                _errorLiveData.postValue("Error fetching posts: ${e.message}")
            } finally {
                isLoading = false
                _loadingLiveData.value = false // Set loading state to false after loading completes
            }
        }
    }*/

    fun fetchPostsByCategory(categoryId: Int) {
        if (isLoading) return  // Prevent multiple simultaneous loads
        isLoading = true
        _loadingLiveData.value = true

        // Reset pagination and posts if a new category is selected
        if (currentCategoryId != categoryId) {
            currentCategoryId = categoryId
            currentPage = 1
            _postsLiveData.value = emptyList()  // Clear current posts for the new category
        }

        viewModelScope.launch {
            try {
                // Fetch new posts by category from repository
                val newPosts = postRepository.getPostsByCategory(categoryId, currentPage)

                // Get the current list of posts from LiveData
                val currentPosts = _postsLiveData.value.orEmpty()

                // Append new posts and sort by date (latest first)
                val updatedPosts = (currentPosts + newPosts).distinctBy { it.id }  // Avoid duplicates
                    .sortedByDescending { post -> post.date }

                // Update LiveData with sorted posts
                _postsLiveData.postValue(updatedPosts)

                currentPage++  // Increment page for future fetches
            } catch (e: Exception) {
                _errorLiveData.postValue("Error fetching posts: ${e.message}")
            } finally {
                isLoading = false
                _loadingLiveData.value = false  // Hide loading indicator
            }
        }
    }



    fun fetchPostDetails(postId: Long) {
        viewModelScope.launch {
            try {
                val postDetails = postRepository.getPostDetails(postId)
                _postDetailsLiveData.postValue(postDetails)
            } catch (e: Exception) {
                _errorLiveData.postValue("Error fetching post details: ${e.message}")
            }
        }
    }

    fun resetPagination() {
        currentPage = 1
        _postsLiveData.value = emptyList()
    }

    fun resetLoadingState() {
        isLoading = false
        _loadingLiveData.value = false
    }

    suspend fun getMatchingPosts(query: String): List<Post> {
        return postRepository.searchPosts(query)  // You already have a search method in the repository
    }

    fun searchPosts(query: String) {
        viewModelScope.launch {
            _loadingLiveData.postValue(true)  // Show loader when fetching starts
            try {
                val posts = postRepository.searchPosts(query)
                _searchResultsLiveData.postValue(posts)
            } catch (e: Exception) {
                _searchResultsLiveData.postValue(emptyList())
            } finally {
                _loadingLiveData.postValue(false)  // Hide loader when fetching ends
            }
        }
    }
}


/*
package com.tohed.islampro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val postRepository = PostRepository(application)

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>>
        get() = _postsLiveData

    private val _postDetailsLiveData = MutableLiveData<Post>()
    val postDetailsLiveData: LiveData<Post>
        get() = _postDetailsLiveData

    private var nextPage = 1

    fun fetchPosts() {
        viewModelScope.launch {
            val posts = postRepository.getPosts(nextPage)
            _postsLiveData.postValue(posts)
            nextPage++
        }
    }

    fun fetchPostDetails(postId: Long) {
        viewModelScope.launch {
            val postDetails = postRepository.getPostDetails(postId)
            _postDetailsLiveData.postValue(postDetails)
        }
    }
}


*/