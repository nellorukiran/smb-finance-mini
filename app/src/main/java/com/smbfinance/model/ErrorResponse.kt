package com.smbfinance.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("status")
    val status: String
) 