package com.tohed.islampro.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.datamodel.Content
import com.tohed.islampro.datamodel.HomePageContent
import com.tohed.islampro.datamodel.Page
import com.tohed.islampro.datamodel.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel() {

    private val postApiService = PostApiService.getService()

    val homePageData = MutableLiveData<HomePageContent>()

    fun loadHomePageData() {
        postApiService.getPageBySlug("home").enqueue(object : Callback<List<Page>> {
            override fun onResponse(call: Call<List<Page>>, response: Response<List<Page>>) {
                if (response.isSuccessful) {
                    response.body()?.firstOrNull()?.let { page ->
                        // Assume page content contains a list of posts specifically for the home page
                        val homePageContent = HomePageContent(
                            rendered = page.content.rendered,
                            posts = fetchPostsFromContent(page.content) // Implement a method to extract posts
                        )
                        homePageData.postValue(homePageContent)
                    }
                }
            }

            override fun onFailure(call: Call<List<Page>>, t: Throwable) {
                Log.e("HomeViewModel", "Failed to load home page data", t)
            }
        })
    }

    // Dummy method to simulate fetching posts from page content
    private fun fetchPostsFromContent(content: Content): List<Post> {
        // You would need to replace this with your actual logic to parse posts from content
        return listOf() // Return the list of posts
    }
}
