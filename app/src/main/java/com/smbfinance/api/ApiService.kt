package com.smbfinance.api

import com.smbfinance.model.CustomerDetailsResponse
import com.smbfinance.model.CustomerStatsResponse
import com.smbfinance.model.LoginRequest
import com.smbfinance.model.LoginResponse
import com.smbfinance.model.RegisterRequest
import com.smbfinance.model.RegisterResponse
import com.smbfinance.model.PaymentHistoryResponse
import com.smbfinance.model.PaymentUpdateRequest
import com.smbfinance.model.CustomerListResponse
import com.smbfinance.model.Customer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/customer-stats")
    suspend fun getCustomerStats(): Response<CustomerStatsResponse>

    @GET("api/customer/payment-details/search")
    suspend fun getPaymentHistory(@Query("customerId") customerId: String): Response<PaymentHistoryResponse>

    @GET("api/customer/details")
    suspend fun getCustomerDetails(@Query("customerId") customerId: String): Response<CustomerDetailsResponse>

    @POST("api/customer/payment/update")
    suspend fun updatePayment(@Body request: PaymentUpdateRequest): Response<LoginResponse>

    @POST("api/logout")
    suspend fun logout(): Response<LoginResponse>

    @POST("api/customer/add")
    suspend fun addCustomer(@Body customer: Customer): Response<LoginResponse>

    @GET("api/customers")
    suspend fun getCustomers(): Response<CustomerListResponse>
} 