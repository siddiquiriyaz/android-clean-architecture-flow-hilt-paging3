package com.premitivekey.cleanarchitecturedemo.domain.usecase

import com.premitivekey.cleanarchitecturedemo.domain.model.PagedUsers
import com.premitivekey.cleanarchitecturedemo.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(private val repository: UserRepository) {

    fun execute(page: Int): Flow<PagedUsers> = repository.getUsers(page)

}