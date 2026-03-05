package com.premitivekey.cleanarchitecturedemo.data.mapper

import com.premitivekey.cleanarchitecturedemo.data.model.CreateUserRequestDto
import com.premitivekey.cleanarchitecturedemo.data.model.CreateUserResponseDto
import com.premitivekey.cleanarchitecturedemo.data.model.UserDto
import com.premitivekey.cleanarchitecturedemo.data.model.UserResponse
import com.premitivekey.cleanarchitecturedemo.domain.model.CreateUserRequest
import com.premitivekey.cleanarchitecturedemo.domain.model.PagedUsers
import com.premitivekey.cleanarchitecturedemo.domain.model.User

fun UserDto.toDomain(): User = User(
    id          = id,
    firstName   = firstName,
    lastName    = lastName,
    email       = email,
    phone       = phone,
    image       = image,
    companyName = company?.name,
)

fun UserResponse.toDomain(currentPage: Int, pageSize: Int): PagedUsers = PagedUsers(
    users       = users.map { it.toDomain() },
    currentPage = currentPage,
    totalPages  = Math.ceil(total / pageSize.toDouble()).toInt(),
)

fun CreateUserResponseDto.toDomain(): User = User(
    id        = id,
    firstName = firstName,
    lastName  = lastName,
    email     = email,
)

fun CreateUserRequest.toDto(): CreateUserRequestDto = CreateUserRequestDto(
    firstName = firstName,
    lastName  = lastName,
    email     = email,
)