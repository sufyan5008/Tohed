package com.tohed.islampro.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path




/*interface CategoryApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    companion object {
        fun create(): CategoryApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://tohed.com/wp-json/wp/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(CategoryApiService::class.java)
        }
    }
}*/

