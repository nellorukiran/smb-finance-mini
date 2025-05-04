package com.smbfinance.model

data class CollectionResponse(
    val totalAmount: Double,
    val collections: List<Collection>,
    val totalCollections: Int,
    val status: String
)

data class Collection(
    val amount: Double,
    val phoneNumber: String,
    val totalDues: Int,
    val balanceDue: Double,
    val createdBy: String,
    val customerId: String,
    val collectionDate: String,
    val transactionId: String,
    val customerName: String
) 