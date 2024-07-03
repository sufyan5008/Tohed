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
