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


