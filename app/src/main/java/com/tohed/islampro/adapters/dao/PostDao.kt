package com.tohed.islampro.adapters.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tohed.islampro.db.PageEntity
import com.tohed.islampro.db.PostEntity

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: List<PostEntity>)

    @Query("SELECT * FROM posts WHERE categoryId = :categoryId")
    fun getPostsByCategory(categoryId: Int): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Int): PostEntity?

    // Methods for pages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPage(page: PageEntity)

    @Query("SELECT * FROM pages WHERE id = :pageId")
    fun getPageById(pageId: Int): PageEntity?
}

/*@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getAllPosts(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Int): PostEntity

    @Query("SELECT * FROM posts WHERE categoryId = :categoryId")
    fun getPostsByCategory(categoryId: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: List<PostEntity>)
}*/
