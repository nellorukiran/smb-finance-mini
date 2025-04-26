package com.smbfinance.model

data class PaymentUpdateRequest(
    val customerId: String,
    val paidAmount: Double,
    val paidDate: String,
    val penalty: Double
) 