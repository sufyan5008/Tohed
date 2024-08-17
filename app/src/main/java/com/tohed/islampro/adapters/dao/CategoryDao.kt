/*
package com.tohed.islampro.adapters.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tohed.islampro.db.CategoryEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE parentId = :parentId")
    fun getSubcategoriesByParent(parentId: Int): List<CategoryEntity>
}
*/
