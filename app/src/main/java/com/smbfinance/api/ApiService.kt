package com.smbfinance.api

import com.smbfinance.model.CustomerStatsResponse
import com.smbfinance.model.LoginRequest
import com.smbfinance.model.LoginResponse
import com.smbfinance.model.RegisterRequest
import com.smbfinance.model.RegisterResponse
import com.smbfinance.model.PaymentHistoryResponse
import com.smbfinance.model.PaymentUpdateRequest
import com.smbfinance.model.CustomerListResponse
import com.smbfinance.model.Customer
import com.smbfinance.model.CustomerDetailsResponse
import com.smbfinance.model.TransactionDetailsResponse
import com.smbfinance.model.DeleteSearchResponse
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

    @GET("api/transaction/details")
    suspend fun getTransactionDetails(@Query("customerId") customerId: String): Response<TransactionDetailsResponse>

    @POST("api/customer/payment/update")
    suspend fun updatePayment(@Body request: PaymentUpdateRequest): Response<LoginResponse>

    @POST("api/logout")
    suspend fun logout(): Response<LoginResponse>

    @POST("api/customer/add")
    suspend fun addCustomer(@Body customer: Customer): Response<LoginResponse>

    @GET("api/customers")
    suspend fun getCustomers(): Response<CustomerListResponse>

    @GET("api/customer/search")
    suspend fun getCustomerById(@Query("customerId") customerId: String): Response<LoginResponse>

    @GET("api/customer/details")
    suspend fun getCustomerDetails(@Query("customerId") customerId: String): Response<CustomerDetailsResponse>

    @GET("api/customer/delete/search")
    suspend fun deleteSearchCustomer(@Query("customerId") customerId: String): Response<DeleteSearchResponse>

    @POST("api/customer/delete")
    suspend fun deleteCustomer(@Query("customerId") customerId: String): Response<LoginResponse>
} 