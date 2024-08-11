package com.tohed.islampro.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Post
import kotlinx.coroutines.launch

class LeaguesViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val apiService = PostApiService.getService()

    fun fetchPostsByCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getPostsByCategory(categoryId, 1).execute()
                if (response.isSuccessful) {
                    Log.d("LeaguesViewModel", "Posts fetched: ${response.body()}")
                    _posts.postValue(response.body())
                } else {
                    Log.d("LeaguesViewModel", "Failed response: ${response.errorBody()}")
                    _posts.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("LeaguesViewModel", "Exception occurred: $e")
                _posts.postValue(emptyList())
            }
        }
    }

}
