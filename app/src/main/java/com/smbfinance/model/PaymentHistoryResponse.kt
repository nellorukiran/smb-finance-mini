package com.smbfinance.model

data class PaymentHistoryResponse(
    val transactions: List<Transaction>,
    val customerDetails: CustomerDetails
)

data class Transaction(
    val transactionId: String,
    val customerId: String,
    val paidDue: Double,
    val paidDate: String,
    val transactionDate: String,
    val balanceDue: Double,
    val createdBy: String?,
    val createdDate: String?,
    val updatedBy: String?,
    val updatedDate: String?
)

data class CustomerDetails(
    val nextDueAmount: Double,
    val phoneNumber: String,
    val address: String,
    val totalDues: Int,
    val totalDueAmount: Double,
    val perMonthDue: Double,
    val penalty: Double,
    val customerName: String,
    val custStatus: String
) 