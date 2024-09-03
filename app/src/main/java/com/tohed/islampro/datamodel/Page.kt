package com.tohed.islampro.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Page(
    val id: Int,
    val title: Title,
    val content: Content
) : Parcelable
