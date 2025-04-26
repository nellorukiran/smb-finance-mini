package com.smbfinance.api

import com.smbfinance.model.Customer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomerService {
    @POST("customers")
    fun addCustomer(@Body customer: Customer): Call<Customer>
} 