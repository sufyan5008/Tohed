package com.tohed.islampro.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tohed.islampro.api.CategoryApiService
import com.tohed.islampro.datamodel.Category

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class CategoryRepository(private val categoryApiService: CategoryApiService) {
    suspend fun getCategoryDetail(categoryId: Int): Category {
        return categoryApiService.getCategoryDetail(categoryId).body() ?: throw Exception("Category detail not found")
    }
}


/*class CategoryRepository(
    private val categoryApiService: CategoryApiService,
    private val coroutineScope: CoroutineScope
) {

    fun getCategoryDetail(categoryId: Int): LiveData<Category?> {
        val categoryLiveData = MutableLiveData<Category?>()
        coroutineScope.launch {
            try {
                val response = categoryApiService.getCategoryDetail(categoryId)
                if (response.isSuccessful) {
                    val categoryDetail = response.body()
                    categoryLiveData.postValue(categoryDetail)
                }
            } catch (e: Exception) {
                Log.e("CategoryRepository", "Error: ${e.message}")
            }
        }
        return categoryLiveData
    }

}*/
