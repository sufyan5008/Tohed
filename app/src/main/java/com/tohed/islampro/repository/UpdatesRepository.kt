package com.tohed.islampro.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
class UpdatesRepository(private val context: Context) {
    private val postApiService = PostApiService.getService()
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "post-database"
    ).build()

    suspend fun getPostsByCategory(categoryId: Int): List<Post> {
        return withContext(Dispatchers.IO) {
            val cachedPosts = db.postDao().getPostsByCategory(categoryId).map { it.toDomain() }

            if (isOnline(context)) {
                try {
                    val response = postApiService.getPostsByCategory(categoryId, 1).execute()
                    if (response.isSuccessful) {
                        val posts = response.body() ?: emptyList()
                        db.postDao().insertPosts(posts.map { it.toEntity() })
                        posts
                    } else {
                        cachedPosts
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    cachedPosts
                }
            } else {
                cachedPosts
            }
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
*/
