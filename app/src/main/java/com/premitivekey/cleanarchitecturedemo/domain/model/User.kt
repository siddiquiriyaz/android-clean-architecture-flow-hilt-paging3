package com.premitivekey.cleanarchitecturedemo.domain.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val image: String? = null,
    val companyName: String? = null,
)