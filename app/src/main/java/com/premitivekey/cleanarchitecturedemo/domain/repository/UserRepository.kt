package com.premitivekey.cleanarchitecturedemo.domain.repository

import com.premitivekey.cleanarchitecturedemo.domain.model.CreateUserRequest
import com.premitivekey.cleanarchitecturedemo.domain.model.PagedUsers
import com.premitivekey.cleanarchitecturedemo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(page: Int): Flow<PagedUsers>
    fun createUser(request: CreateUserRequest): Flow<User>
}