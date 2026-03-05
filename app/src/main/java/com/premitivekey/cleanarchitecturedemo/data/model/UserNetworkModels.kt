package com.premitivekey.cleanarchitecturedemo.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("users") val users: List<UserDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("skip")  val skip: Int,
    @SerializedName("limit") val limit: Int,
)

data class UserDto(
    @SerializedName("id")        val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName")  val lastName: String,
    @SerializedName("email")     val email: String,
    @SerializedName("phone")     val phone: String? = null,
    @SerializedName("image")     val image: String? = null,
    @SerializedName("company")   val company: CompanyDto? = null,
)

data class CompanyDto(
    @SerializedName("name")       val name: String? = null,
    @SerializedName("department") val department: String? = null,
)

data class CreateUserRequestDto(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName")  val lastName: String,
    @SerializedName("email")     val email: String,
)

data class CreateUserResponseDto(
    @SerializedName("id")        val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName")  val lastName: String,
    @SerializedName("email")     val email: String,
)