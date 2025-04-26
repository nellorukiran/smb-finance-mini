package com.smbfinance.model

data class CustomerDetailsResponse(
    val status: String,
    val data: CustomerDetailsData?
)

data class CustomerDetailsData(
    val customerId: String,
    val customerName: String,
    val address: String,
    val phoneNumber: String,
    val purchaseDate: String,
    val shopName: String,
    val productName: String,
    val productModel: String,
    val actualPrice: Double,
    val salePrice: Double,
    val totalDues: Int,
    val advance: Double,
    val penalty: Double,
    val purchaseDateStr: String,
    val dueTime: String,
    val dueAmount: Double,
    val createdDate: String?,
    val totalDueAmount: Double,
    val perMonthDue: Double,
    val interestAmount: Double,
    val profit: Double,
    val docCharges: Double,
    val totalProfit: Double,
    val custStatus: String,
    val aadharNumber: String?,
    val updatedDate: String?,
    val createdBy: String?,
    val updatedBy: String?
) 