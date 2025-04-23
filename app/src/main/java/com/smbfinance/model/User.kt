package com.smbfinance.model

import java.io.Serializable

data class User(
    val username: String,
    val email: String,
    val fullName: String
) : Serializable 