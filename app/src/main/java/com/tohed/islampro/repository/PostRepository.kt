package com.tohed.islampro.repository


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Content
import com.tohed.islampro.datamodel.Excerpt
import com.tohed.islampro.datamodel.Page
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.datamodel.Title
import com.tohed.islampro.db.AppDatabase
import com.tohed.islampro.db.PostEntity
import com.tohed.islampro.db.toDomain
import com.tohed.islampro.db.toEntity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class PostRepository(private val context: Context) {
    private val postApiService = PostApiService.getService()
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "post-database"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()) // Adjust format as needed
    private fun parseDate(dateString: String): Long? {
        return try {
            dateFormat.parse(dateString)?.time
        } catch (e: ParseException) {
            null
        }
    }

    suspend fun getPosts(page: Int): List<Post> = withContext(Dispatchers.IO) {
        val cachedPosts = db.postDao().getPostsByCategory(0).map { it.toDomain() }

        val postsFromApi = if (isOnline(context)) {
            val response = postApiService.getPosts(page).execute()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                // Insert new posts into the local database
                db.postDao().insertPosts(posts.map { it.toEntity(0) })
                posts
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }

        // Merge cached posts with the new posts
        val allPosts = (cachedPosts + postsFromApi).distinctBy { it.id }

        // Sort posts by date, latest first
        allPosts.sortedByDescending { parseDate(it.date) ?: Long.MIN_VALUE }
    }

    suspend fun getPostsByCategory(categoryId: Int, page: Int): List<Post> = withContext(Dispatchers.IO) {
        val cachedPosts = db.postDao().getPostsByCategory(categoryId).map { it.toDomain() }

        if (cachedPosts.isNotEmpty()) {
            // Return cached data immediately if available
            return@withContext cachedPosts
        }
        // Attempt to fetch from the network
        if (isOnline(context)) {
            val response = postApiService.getPostsByCategory(categoryId, page).execute()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                db.postDao().insertPosts(posts.map { it.toEntity(categoryId) })
                posts
            } else {
                cachedPosts // Fallback to cached data
            }
        } else {
            cachedPosts // Fallback to cached data
        }
    }

    suspend fun getPostDetails(postId: Long): Post = withContext(Dispatchers.IO) {
        (if (isOnline(context)) {
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
            db.postDao().getPostById(postId.toInt())?.toDomain()
        })!!
    }

    suspend fun getPageDetails(pageId: Long): Page? = withContext(Dispatchers.IO) {
        if (isOnline(context)) {
            val response = postApiService.getPageById(pageId).execute()
            if (response.isSuccessful) {
                val page = response.body()
                page?.let {
                    db.postDao().insertPage(it.toEntity())
                }
                page
            } else {
                // Return cached content in case of network error
                db.postDao().getPageById(pageId.toInt())?.toDomain()
            }
        } else {
            // Return cached content if offline
            db.postDao().getPageById(pageId.toInt())?.toDomain()
        }
    }
    fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFC)  // Use NFC form for normalization
    }
    /*suspend fun searchPosts(query: String): List<Post> = withContext(Dispatchers.IO) {
        if (isOnline(context)) {
            val response = postApiService.searchPosts(query).execute()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()

                // Normalize the query before filtering
                val normalizedQuery = normalizeText(query)

                // Apply filtering by title, normalize both the title and query
                val filteredPosts = posts.filter { post ->
                    normalizeText(post.title.rendered).contains(normalizedQuery, ignoreCase = true)
                }

                // Cache the API results locally
                db.postDao().insertPosts(filteredPosts.map { it.toEntity(0) })

                // Return the filtered and sorted posts (latest first)
                filteredPosts.sortedByDescending { parseDate(it.date) ?: Long.MIN_VALUE }
            } else {
                emptyList()  // Return an empty list if API call fails
            }
        } else {
            val normalizedQuery = normalizeText(query)

            // If offline, fetch from the local database and normalize title for comparison
            db.postDao().searchPostsByTitle(query).map { it.toDomain() }
                .filter { post -> normalizeText(post.title.rendered).contains(normalizedQuery, ignoreCase = true) }
                .sortedByDescending { parseDate(it.date) ?: Long.MIN_VALUE }
        }
    }*/
    suspend fun searchPosts(query: String): List<Post> = withContext(Dispatchers.IO) {
        if (isOnline(context)) {
            var page = 1
            val allPosts = mutableListOf<Post>()

            do {
                // Fetch posts for the current page
                val response = postApiService.searchPosts(query, page).execute()
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()

                    // Normalize the query before filtering
                    val normalizedQuery = normalizeText(query)

                    // Filter posts by title
                    val filteredPosts = posts.filter { post ->
                        normalizeText(post.title.rendered).contains(normalizedQuery, ignoreCase = true)
                    }

                    // Add the filtered posts to the list
                    allPosts.addAll(filteredPosts)

                    // Cache the filtered posts locally (optional)
                    db.postDao().insertPosts(filteredPosts.map { it.toEntity(0) })

                    // If fewer posts were returned than expected, stop fetching
                    if (posts.size < 10) break  // Assuming 10 posts per page, adjust as per your API limit
                } else {
                    break  // Stop if the response is not successful
                }

                // Move to the next page
                page++

            } while (true)  // Continue fetching until no more posts

            // Sort all the posts by date (latest first) and return
            allPosts.sortedByDescending { parseDate(it.date) ?: Long.MIN_VALUE }
        } else {
            // If offline, fetch from the local database
            val normalizedQuery = normalizeText(query)
            db.postDao().searchPostsByTitle(query).map { it.toDomain() }
                .filter { post -> normalizeText(post.title.rendered).contains(normalizedQuery, ignoreCase = true) }
                .sortedByDescending { parseDate(it.date) ?: Long.MIN_VALUE }
        }
    }




    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}