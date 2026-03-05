package com.premitivekey.cleanarchitecturedemo.domain.model

data class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
)