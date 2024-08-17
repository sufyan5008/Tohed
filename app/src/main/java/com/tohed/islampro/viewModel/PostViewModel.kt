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
    var currentCategoryTitle: String? = null

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>>
        get() = _postsLiveData

    private val _postDetailsLiveData = MutableLiveData<Post>()
    val postDetailsLiveData: LiveData<Post>
        get() = _postDetailsLiveData

    private var currentPage = 1
    private var isLoading = false

    private var currentCategoryId: Int? = null
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    fun fetchPosts() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val posts = postRepository.getPosts(currentPage)
            val currentPosts = _postsLiveData.value ?: emptyList()
            _postsLiveData.postValue(posts)
            currentPage++
            isLoading = false
        }
    }

    /*fun fetchPostsByCategory(categoryId: Int) {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val posts = postRepository.getPostsByCategory(categoryId, currentPage)
            _postsLiveData.postValue(posts)
            currentPage++
            isLoading = false
        }
    }*/
    fun fetchPostsByCategory(categoryId: Int) {
        if (isLoading) return
        isLoading = true

        _loadingLiveData.value = true // Set loading state to true when starting

        // Reset current category and pagination if a new category is selected
        if (currentCategoryId != categoryId) {
            currentCategoryId = categoryId
            currentPage = 1
            _postsLiveData.value = emptyList() // Clear the LiveData to reset the UI
        }

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
    }


    fun fetchPostDetails(postId: Long) {
        viewModelScope.launch {
            val postDetails = postRepository.getPostDetails(postId)
            _postDetailsLiveData.postValue(postDetails)
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