package com.tohed.islampro.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch


/*class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    private val _categoryDetail = MutableLiveData<Category>()
    val categoryDetail: LiveData<Category> = _categoryDetail

    fun getCategoryDetail(categoryId: Int) {
        viewModelScope.launch {
            try {
                val result = categoryRepository.getCategoryDetail(categoryId)
                _categoryDetail.value = result
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}*/




/*class CategoryViewModel : ViewModel() {
    private val categoryApiService: CategoryApiService = CategoryApiService.create()

    val categoriesLiveData: MutableLiveData<List<Category>> = MutableLiveData()

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val categories = categoryApiService.getCategories()
                categoriesLiveData.postValue(categories)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}*/
