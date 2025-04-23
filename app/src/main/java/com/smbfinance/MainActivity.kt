package com.smbfinance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.ErrorResponse
import com.smbfinance.model.LoginRequest
import com.smbfinance.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

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
                performLogin(username, password)
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginRequest = LoginRequest(username, password)
                Log.d(TAG, "Login Request: $loginRequest")
                
                val response = RetrofitClient.apiService.login(loginRequest)
                
                // Log response details
                Log.d(TAG, "Login Response Status: ${response.code()} (${getStatusMessage(response.code())})")
                Log.d(TAG, "Login Response Headers: ${response.headers()}")
                
                // Log the raw response body for debugging
                val errorBody = response.errorBody()?.string()
                Log.d(TAG, "Login Error Body: ${errorBody ?: "No error body"}")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d(TAG, "Login Response Body: $responseBody")
                        
                        // Create user object with login details
                        val user = User(
                            username = username,
                            email = "$username@smbfinance.com",
                            fullName = username.replaceFirstChar { it.uppercase() }
                        )
                        
                        Log.d(TAG, "Created User Object: $user")
                        
                        // Clear input fields
                        usernameInput.text.clear()
                        passwordInput.text.clear()
                        
                        // Navigate to dashboard with user data
                        val intent = Intent(this@MainActivity, DashboardActivity::class.java).apply {
                            putExtra("user", user)
                        }
                        startActivity(intent)
                        finish() // Close the login activity
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
                        
                        Log.e(TAG, "Login failed with status: ${response.code()} (${getStatusMessage(response.code())})")
                        Log.e(TAG, "Error details: $errorMessage")
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
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