package com.smbfinance.model

data class CustomerResponse(
    val success: Boolean,
    val message: String,
    val data: CustomerData?
)

data class CustomerData(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val aadharNumber: String,
    val panNumber: String,
    val accountType: String,
    val accountStatus: String,
    val createdAt: String,
    val updatedAt: String
) 