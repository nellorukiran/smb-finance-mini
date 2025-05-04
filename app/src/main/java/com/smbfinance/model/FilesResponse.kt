package com.smbfinance.model

data class FilesResponse(
    val totalDocCharges: Double,
    val totalInterestAmount: Double,
    val totalProfit: Double,
    val dueAmount: Double,
    val totalDueAmount: Double,
    val files: List<File>,
    val totalFiles: Int,
    val profit: Double,
    val status: String
)

data class File(
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
    val createdDate: String,
    val totalDueAmount: Double,
    val perMonthDue: Double,
    val interestAmount: Double,
    val profit: Double,
    val docCharges: Double,
    val totalProfit: Double,
    val custStatus: String,
    val aadharNumber: String?,
    val updatedDate: String,
    val createdBy: String,
    val updatedBy: String
) 