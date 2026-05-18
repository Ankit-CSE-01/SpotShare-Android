package com.spotshare.domain.model

data class Review(
    val id: String,
    val spotId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)
