package com.smbfinance.model

data class Customer(
    val id: Long? = null,
    val name: String,
    val phone: String,
    val address: String,
    val amount: Double,
    val interest: Double,
    val duration: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
) 