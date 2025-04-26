package com.smbfinance.model

import com.google.gson.annotations.SerializedName

data class CustomerListResponse(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<Customer>
) 