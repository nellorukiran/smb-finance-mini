package com.smbfinance

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.ErrorResponse
import com.smbfinance.model.LoginRequest
import com.smbfinance.model.LoginResponse
import com.smbfinance.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val gson = Gson()
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerLink = findViewById(R.id.register_link)
        
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            
            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    performLogin(username, password)
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun performLogin(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginRequest = LoginRequest(username, password)
                Log.d(TAG, "Login Request: $loginRequest")
                
                val response = RetrofitClient.apiService.login(loginRequest)
                
                // Log response details
                Log.d(TAG, "Login Response Status: ${response.code()}")
                Log.d(TAG, "Login Response Headers: ${response.headers()}")
                
                // Log the raw response body for debugging
                val responseBody = response.body()
                val errorBody = response.errorBody()?.string()
                Log.d(TAG, "Login Response Body: $responseBody")
                Log.d(TAG, "Login Error Body: $errorBody")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        if (responseBody != null) {
                            Log.d(TAG, "Login Response Status: ${responseBody.status}")
                            Log.d(TAG, "Login Response Message: ${responseBody.message}")
                            
                            if (responseBody.status == "success") {
                                //val userData = responseBody.data.user
                               // Log.d(TAG, "User Data: $userData")
                                
//                                val user = User(
//                                    userData.id,
//                                    userData.username,
//                                    userData.email,
//                                    userData.fullName,
//                                    userData.userType
//                                )
                                
                                // Store the auth token
                                //RetrofitClient.setAuthToken(responseBody.data.token)
                               // Log.d(TAG, "Auth token stored: ${responseBody.data.token}")
                                
                                // Clear input fields
                                usernameInput.text.clear()
                                passwordInput.text.clear()
                                
                                try {
                                    // Navigate to dashboard without user data
                                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    Log.d(TAG, "Starting DashboardActivity")
                                    startActivity(intent)
                                    Log.d(TAG, "DashboardActivity started successfully")
                                    finish() // Close the login activity
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error navigating to DashboardActivity", e)
                                    Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
                                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val errorMessage = responseBody.message ?: "Invalid response format"
                                Log.e(TAG, "Login failed: $errorMessage")
                                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Log.e(TAG, "Login response body is null")
                            Toast.makeText(this@MainActivity, "Login failed: Invalid response", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorMessage = try {
                            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            when (response.code()) {
                                401 -> "Invalid username or password"
                                400 -> "Bad request: Please check your input"
                                500 -> "Server error: Please try again later"
                                else -> "Login failed: Unknown error"
                            }
                        }
                        
                        Log.e(TAG, "Login failed with status: ${response.code()}")
                        Log.e(TAG, "Error details: $errorMessage")
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Network error: Cannot connect to server", e)
                    Toast.makeText(this@MainActivity, "Cannot connect to server. Please check your internet connection.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Login Error", e)
                    Log.e(TAG, "Error message: ${e.message}")
                    Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getStatusMessage(code: Int): String {
        return when (code) {
            200 -> "OK"
            201 -> "Created"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            500 -> "Internal Server Error"
            else -> "Unknown Status"
        }
    }
}