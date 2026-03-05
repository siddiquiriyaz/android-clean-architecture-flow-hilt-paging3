package com.premitivekey.cleanarchitecturedemo.domain.model

data class PagedUsers(
    val users: List<User>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean = currentPage < totalPages,
)