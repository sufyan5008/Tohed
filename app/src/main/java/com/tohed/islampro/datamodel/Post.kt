package com.tohed.islampro.datamodel

data class Post(
    val id: Int,
    val title: Title,
    val date: String,
    val content: Content

)

data class Title(
    val rendered: String
)
data class Content(
    val rendered: String,
    val html: String

)

data class Category(
    val title: String,
    val content: String
)

