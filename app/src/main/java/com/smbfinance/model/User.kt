package com.smbfinance.model

import java.io.Serializable

data class User(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val userType: String
) : Serializable 