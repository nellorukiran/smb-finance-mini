package com.smbfinance.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("data")
    val data: LoginData? = null
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
    
    @SerializedName("fullName")
    val fullName: String,
    
    @SerializedName("userType")
    val userType: String
) 