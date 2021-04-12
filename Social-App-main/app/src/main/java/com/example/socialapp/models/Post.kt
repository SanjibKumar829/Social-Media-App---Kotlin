package com.example.socialapp.models

data class Post (
    val text: String = "",
    val imageurl: String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    val likedBy: ArrayList<String> = ArrayList())