package com.tohed.islampro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.repository.PostRepository
import kotlinx.coroutines.launch

class UpdatesViewModel(application: Application) : AndroidViewModel(application) {

    private val postRepository = PostRepository(application)
    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>> get() = _postsLiveData

    private var currentPage = 1
    private var isLoading = false


    // Fetch posts with offline support for category 504
    fun fetchPostsByCategory(categoryId: Int) {
        if (isLoading) return  // Prevent multiple fetches at once
        isLoading = true

        viewModelScope.launch {
            try {
                // Always fetch posts for category 504
                val posts = postRepository.getPostsByCategory(categoryId, currentPage)

                // Update LiveData with the fetched posts
                _postsLiveData.postValue(posts)

                currentPage++  // Increment page for the next fetch
            } catch (e: Exception) {
                // Handle error (optional error handling can be added)
            } finally {
                isLoading = false
            }
        }
    }

    // Sync posts only when online (for category 504)
    fun syncPostsByCategory(categoryId: Int) {
        if (postRepository.isOnline(getApplication())) {
            fetchPostsByCategory(categoryId)  // Fetch new posts if online
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

class UpdatesViewModel(application: Application) : AndroidViewModel(application) {

    private val postRepository = PostRepository(application)

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>>
        get() = _postsLiveData

    private var nextPage = 1
    private var isLoading = false

    fun fetchPostsByCategory(categoryId: Int) {
        viewModelScope.launch {
            val posts = postRepository.getPostsByCategory(categoryId, nextPage)
            _postsLiveData.postValue(posts)
        }
    }

    fun syncPostsByCategory(categoryId: Int) {
        if (postRepository.isOnline(getApplication())) {
            fetchPostsByCategory(categoryId)
        }
    }
}
*/
