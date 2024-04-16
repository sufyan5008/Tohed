package com.tohed.islampro.api
// PostApiService.kt
import com.tohed.islampro.datamodel.Post
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {
    @GET("posts")
    fun getPosts(@Query("page") page: Int): Call<List<Post>>

    @GET("posts/{postId}")
    fun getPostById(@Path("postId") postId: Long): Call<Post>

    companion object {
        private const val BASE_URL = "https://tohed.com/wp-json/wp/v2/"

        fun getService(): PostApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(PostApiService::class.java)
        }
    }
}