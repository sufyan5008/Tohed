package com.tohed.islampro.repository


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Content
import com.tohed.islampro.datamodel.Excerpt
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.datamodel.Title
import com.tohed.islampro.db.AppDatabase
import com.tohed.islampro.db.PostEntity
import com.tohed.islampro.db.toDomain
import com.tohed.islampro.db.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(private val context: Context) {
    private val postApiService = PostApiService.getService()
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "post-database"
    ).build()

    /*suspend fun getPosts(page: Int): List<Post> = withContext(Dispatchers.IO) {
        if (isOnline(context)) {
            val response = postApiService.getPosts(page).execute()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                db.postDao().insertPosts(posts.map { it.toEntity(0) }) // Assuming category ID 0 for all posts
                posts
            } else {
                emptyList()
            }
        } else {
            db.postDao().getPostsByCategory(0).map { it.toDomain() }
        }
    }*/

    suspend fun getPosts(page: Int): List<Post> = withContext(Dispatchers.IO) {
        val cachedPosts = db.postDao().getPostsByCategory(0).map { it.toDomain() }

        if (isOnline(context)) {
            val response = postApiService.getPosts(page).execute()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                // Insert new posts into the local database
                db.postDao().insertPosts(posts.map { it.toEntity(0) })
                // Merge cached posts with the new posts
                val allPosts = (cachedPosts + posts).distinctBy { it.id }
                allPosts
            } else {
                cachedPosts
            }
        } else {
            cachedPosts
        }
    }

    suspend fun getPostsByCategory(categoryId: Int, page: Int): List<Post> = withContext(Dispatchers.IO) {
        val cachedPosts = db.postDao().getPostsByCategory(categoryId).map { it.toDomain() }

        if (isOnline(context)) {
            val response = postApiService.getPostsByCategory(categoryId, page).execute()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                db.postDao().insertPosts(posts.map { it.toEntity(categoryId) })
                val allPosts = (cachedPosts + posts).distinctBy { it.id }
                allPosts
            } else {
                cachedPosts
            }
        } else {
            cachedPosts
        }
    }

    suspend fun getPostDetails(postId: Long): Post = withContext(Dispatchers.IO) {
        if (isOnline(context)) {
            val response = postApiService.getPostById(postId).execute()
            if (response.isSuccessful) {
                val post = response.body()
                post?.let {
                    db.postDao().insertPosts(listOf(it.toEntity(0))) // Assuming category ID 0 for all posts
                }
                post ?: Post(0, Title(""), "", Content("", ""), Excerpt(""))
            } else {
                Post(0, Title(""), "", Content("", ""), Excerpt(""))
            }
        } else {
            db.postDao().getPostById(postId.toInt()).toDomain()
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
/*    suspend fun getPosts(page: Int): List<Post> {
        return withContext(Dispatchers.IO) {
            if (isOnline(context)) {
                val response = postApiService.getPosts(page).execute()
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    // Cache posts locally
                    db.postDao().insertPosts(posts.map { it.toEntity() })
                    posts
                } else {
                    emptyList()
                }
            } else {
                db.postDao().getAllPosts().map { it.toDomain() }
            }
        }
    }

    suspend fun getPostDetails(postId: Long): Post {
        return withContext(Dispatchers.IO) {
            if (isOnline(context)) {
                val response = postApiService.getPostById(postId).execute()
                if (response.isSuccessful) {
                    val post = response.body()
                    post?.let {
                        // Cache post details locally
                        db.postDao().insertPosts(listOf(it.toEntity()))
                    }
                    post ?: Post(0, Title(""), "", Content("", ""))
                } else {
                    Post(0, Title(""), "", Content("", ""))
                }
            } else {
                db.postDao().getPostById(postId.toInt()).toDomain()
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

private fun Post.toEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        title = this.title.rendered,
        date = this.date,
        content = this.content.rendered
    )
}

private fun PostEntity.toDomain(): Post {
    return Post(
        id = this.id,
        title = Title(this.title),
        date = this.date,
        content = Content(this.content, "")
    )
}*/
