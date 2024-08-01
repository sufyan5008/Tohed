package com.tohed.islampro.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tohed.islampro.datamodel.Category
import com.tohed.islampro.datamodel.Content
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.datamodel.Title

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val date: String,
    val content: String,
    val categoryId: Int
)
fun Post.toEntity(categoryId: Int) = PostEntity(
    id = this.id,
    title = this.title.rendered,
    date = this.date,
    content = this.content.rendered,
    categoryId = categoryId
)

fun PostEntity.toDomain() = Post(
    id = this.id,
    title = Title(this.title),
    date = this.date,
    content = Content(this.content, html = "")
)

/*fun Category.toEntity() = CategoryEntity(
    id = this.id,
    title = this.title,
    content = this.content,
    count = this.count,
    description = this.description,
    link = this.link,
    name = this.name,
    slug = this.slug,
    taxonomy = this.taxonomy,
    parent = this.parent,
    meta = this.meta
)

fun CategoryEntity.toDomain() = Category(
    title = this.title,
    content = this.content,
    id = this.id,
    count = this.count,
    description = this.description,
    link = this.link,
    name = this.name,
    slug = this.slug,
    taxonomy = this.taxonomy,
    parent = this.parent,
    meta = this.meta)*/