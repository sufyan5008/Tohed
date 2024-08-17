/*
package com.tohed.islampro.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tohed.islampro.datamodel.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val parentId: Int
)

fun Category.toEntity() = CategoryEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    parentId = this.parent
)

fun CategoryEntity.toDomain() = Category(
    id = this.id,
    title = "",
    content = "",
    count = 0,
    description = this.description,
    link = "",
    name = this.name,
    slug = "",
    taxonomy = "",
    parent = this.parentId,
    meta = emptyList()
)
*/
