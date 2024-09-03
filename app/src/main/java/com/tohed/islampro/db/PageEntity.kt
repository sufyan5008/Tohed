package com.tohed.islampro.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tohed.islampro.datamodel.Content
import com.tohed.islampro.datamodel.Page
import com.tohed.islampro.datamodel.Title

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String
)

// Converting PageEntity to and from Page model
fun PageEntity.toDomain(): Page {
    return Page(id, Title(title), Content(rendered = content, html = content))  // Assuming 'html' is the same as 'content'
}

fun Page.toEntity(): PageEntity {
    return PageEntity(id, title.rendered, content.rendered)
}
