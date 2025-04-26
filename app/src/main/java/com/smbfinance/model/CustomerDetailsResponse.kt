package com.smbfinance.model

data class CustomerDetailsResponse(
    val status: String,
    val data: CustomerDetailsData?
)

data class CustomerDetailsData(
    val customerId: String,
    val customerName: String,
    val phoneNumber: String,
    val address: String,
    val productName: String,
    val totalDues: Int,
    val perMonthDue: Double,
    val penalty: Double,
    val purchaseDate: String?,
    val purchaseDateStr: String,
    val dueTime: String,
    val totalDueAmount: Double,
    val nextDueAmount: Double,
    val custStatus: String,
    val createdBy: String,
    val createdDate: String,
    val updatedBy: String,
    val updatedDate: String
) 