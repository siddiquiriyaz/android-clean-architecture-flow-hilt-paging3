package com.premitivekey.cleanarchitecturedemo.data.repository


import com.premitivekey.cleanarchitecturedemo.data.mapper.toDomain
import com.premitivekey.cleanarchitecturedemo.data.mapper.toDto
import com.premitivekey.cleanarchitecturedemo.data.remote.UserApiService
import com.premitivekey.cleanarchitecturedemo.domain.model.CreateUserRequest
import com.premitivekey.cleanarchitecturedemo.domain.model.PagedUsers
import com.premitivekey.cleanarchitecturedemo.domain.model.User
import com.premitivekey.cleanarchitecturedemo.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val api: UserApiService) : UserRepository {

    private val pageSize = 10

    override fun getUsers(page: Int): Flow<PagedUsers> = flow {
        val skip     = (page - 1) * pageSize
        val response = api.getUsers(limit = pageSize, skip = skip)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain(currentPage = page, pageSize = pageSize))
        } else {
            throw Exception("Failed to fetch users — HTTP ${response.code()}")
        }
    }.flowOn(Dispatchers.IO)

    override fun createUser(request: CreateUserRequest): Flow<User> = flow {
        val response = api.createUser(request.toDto())
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception("Failed to create user — HTTP ${response.code()}")
        }
    }.flowOn(Dispatchers.IO)

}