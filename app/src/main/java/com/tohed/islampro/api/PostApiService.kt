package com.tohed.islampro.api

import com.tohed.islampro.datamodel.Category
import com.tohed.islampro.datamodel.Page
import com.tohed.islampro.datamodel.Post
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

interface PostApiService {
    @GET("posts")
    fun getPosts(@Query("page") page: Int): Call<List<Post>>

    @GET("posts")
    fun searchPosts(
        @Query("search") query: String,
        @Query("page") page: Int    // Added page parameter for pagination
    ): Call<List<Post>>

    @GET("posts/{postId}")
    fun getPostById(@Path("postId") postId: Long): Call<Post>

    @GET("posts")
    fun getPostsByCategory(
        @Query("categories") categoryId: Int,
        @Query("page") page: Int
    ): Call<List<Post>>

    @GET("categories")
    fun getSubcategoriesByParent(
        @Query("parent") parentId: Int
    ): Call<List<Category>>

    @GET("pages/{pageId}")
    fun getPageById(@Path("pageId") pageId: Long): Call<Page>

    @GET("pages")
    fun getPageBySlug(@Query("slug") slug: String): Call<List<Page>>

    companion object {
        private const val BASE_URL = "https://tohed.com/wp-json/wp/v2/"

        fun getService(): PostApiService {
            // Create a TrustManager that accepts all certificates
            val trustManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }

            // Initialize SSLContext with the TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(trustManager), SecureRandom())

            // Build OkHttpClient with the custom SSL configuration
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { _, _ -> true }
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            // Build Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create and return the service instance
            return retrofit.create(PostApiService::class.java)
        }
    }
}