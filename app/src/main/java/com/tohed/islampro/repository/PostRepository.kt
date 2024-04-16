package com.tohed.islampro.repository

import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Content
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.datamodel.Title
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepository {

    private val postApiService = PostApiService.getService()

    fun getPosts(page: Int, callback: (List<Post>) -> Unit) {
        postApiService.getPosts(page).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val posts = response.body()
                    callback(posts ?: emptyList())
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                // Handle failure
            }
        })
    }


    fun getPostDetails(postId: Long, callback: (Post) -> Unit) {
        val call = postApiService.getPostById(postId)
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    callback(post ?: Post(0, Title(""), "", Content("", html = ""))) // Assuming Post has a default constructor
                } else {
                    // Handle API error
                    callback(Post(0, Title(""), "", Content("", html = ""))) // Return default Post object
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                // Handle network error
                callback(Post(0, Title(""), "", Content("", html = ""))) // Return default Post object
            }
        })
    }
}

