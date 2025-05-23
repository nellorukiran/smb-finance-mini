package com.smbfinance.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val TAG = "RetrofitClient"
    // Use localhost for API calls
    private const val BASE_URL = "http://10.0.2.2:5000/"
    // private const val BASE_URL = "http://smbfinance.us-east-1.elasticbeanstalk.com/"

    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
        Log.d(TAG, "Auth token set: ${token?.take(10)}...")
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")
        
        authToken?.let { token ->
            requestBuilder.header("Authorization", "Bearer $token")
            Log.d(TAG, "Adding auth token to request: ${token.take(10)}...")
        }
        
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    init {
        Log.d(TAG, "RetrofitClient initialized with base URL: $BASE_URL")
    }
}
