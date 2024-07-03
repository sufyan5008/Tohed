package com.tohed.islampro.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val title: Title,
    val date: String,
    val content: Content
    //val entity: String

) : Parcelable

@Parcelize
data class Title(
    val rendered: String
) : Parcelable
@Parcelize
data class Content(
    val rendered: String,
    val html: String

) : Parcelable

data class Category(
    val title: String,
    val content: String,
    val id: Int,
    val count: Int,
    val description: String,
    val link: String,
    val name: String,
    val slug: String,
    val taxonomy: String,
    val parent: Int,
    val meta: List<Any>
)

