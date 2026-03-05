package com.premitivekey.cleanarchitecturedemo.domain.usecase

import com.premitivekey.cleanarchitecturedemo.domain.model.CreateUserRequest
import com.premitivekey.cleanarchitecturedemo.domain.model.User
import com.premitivekey.cleanarchitecturedemo.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val repository: UserRepository) {

    fun execute(request: CreateUserRequest): Flow<User> = repository.createUser(request)

}