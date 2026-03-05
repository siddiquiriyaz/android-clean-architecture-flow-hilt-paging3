package com.premitivekey.cleanarchitecturedemo.data.remote

import com.premitivekey.cleanarchitecturedemo.data.model.CreateUserRequestDto
import com.premitivekey.cleanarchitecturedemo.data.model.CreateUserResponseDto
import com.premitivekey.cleanarchitecturedemo.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int,
        @Query("skip")  skip: Int,
    ): Response<UserResponse>

    @POST("users/add")
    suspend fun createUser(
        @Body request: CreateUserRequestDto
    ): Response<CreateUserResponseDto>
}