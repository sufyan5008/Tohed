package com.tohed.islampro.api

import com.tohed.islampro.datamodel.Category
import com.tohed.islampro.datamodel.Post
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface PostApiService {
    @GET("posts")
    fun getPosts(@Query("page") page: Int): Call<List<Post>>

    @GET("posts/{postId}")
    fun getPostById(@Path("postId") postId: Long): Call<Post>

    @GET("posts")
    fun getPostsByCategory(
        @Query("categories") categoryId: Int,
        @Query("page") page: Int
    ): Call<List<Post>>

    // Add method to fetch subcategories by parent category ID
    @GET("categories")
    fun getSubcategoriesByParent(
        @Query("parent") parentId: Int
    ): Call<List<Category>>

    companion object {
        private const val BASE_URL = "https://tohed.com/wp-json/wp/v2/"

        fun getService(): PostApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(PostApiService::class.java)
        }
    }
}
