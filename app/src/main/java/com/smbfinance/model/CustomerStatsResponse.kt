package com.smbfinance.model

import com.google.gson.annotations.SerializedName

data class CustomerStatsResponse(
    @SerializedName("data")
    val data: CustomerStatsData,
    
    @SerializedName("status")
    val status: String
)

data class CustomerStatsData(
    @SerializedName("activeCustomers")
    val activeCustomers: Int,
    
    @SerializedName("homeApplianceCustomers")
    val homeApplianceCustomers: Int,
    
    @SerializedName("loanCustomers")
    val loanCustomers: Int
) 