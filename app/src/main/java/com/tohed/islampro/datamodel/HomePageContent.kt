package com.tohed.islampro.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomePageContent(
    val rendered: String,
    val posts: List<Post>
) : Parcelable
