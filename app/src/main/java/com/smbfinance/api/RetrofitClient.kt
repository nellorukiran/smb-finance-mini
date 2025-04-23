package com.smbfinance.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Use localhost for API calls
    private const val BASE_URL = "http://10.0.2.2:5000"
    // private const val BASE_URL = "http://smbfinance.us-east-1.elasticbeanstalk.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
} 