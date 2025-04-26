package com.smbfinance.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val firstname: String,
    val lastname: String,
    val email: String,
    val username: String,
    val role: String,
    val status: String,
    val message: String
)

data class LoginData(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("user")
    val user: UserData
)

data class UserData(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("role")
    val role: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,
    
    @SerializedName("userType")
    val userType: String
) 