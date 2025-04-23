package com.smbfinance.api

import com.smbfinance.model.CustomerStatsResponse
import com.smbfinance.model.LoginRequest
import com.smbfinance.model.LoginResponse
import com.smbfinance.model.RegisterRequest
import com.smbfinance.model.RegisterResponse
import com.smbfinance.model.PaymentHistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/customer-stats")
    suspend fun getCustomerStats(): Response<CustomerStatsResponse>

    @GET("api/customer/payment-details/search")
    suspend fun getPaymentHistory(@Query("customerId") customerId: String): Response<PaymentHistoryResponse>
} 