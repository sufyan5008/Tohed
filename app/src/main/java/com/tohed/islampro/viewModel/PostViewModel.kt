package com.tohed.islampro.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.repository.PostRepository
import kotlinx.coroutines.launch


class PostViewModel : ViewModel() {

    private val postRepository = PostRepository()

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>>
        get() = _postsLiveData

    private val _postDetailsLiveData = MutableLiveData<Post>()
    val postDetailsLiveData: LiveData<Post>
        get() = _postDetailsLiveData

    private var nextPage = 1

    fun fetchPosts() {
        postRepository.getPosts(nextPage) { posts ->
            _postsLiveData.postValue(posts)
            nextPage++
        }
    }
    fun fetchPostDetails(postId: Long) {
        postRepository.getPostDetails(postId) { postDetails ->
            _postDetailsLiveData.postValue(postDetails)
        }
    }
}
