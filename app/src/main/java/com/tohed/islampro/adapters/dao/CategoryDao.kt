package com.tohed.islampro.adapters.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/*
@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE parent = :parentCategoryId")
    suspend fun getSubcategoriesByParent(parentCategoryId: Int): List<CategoryEntity>
}
*/
