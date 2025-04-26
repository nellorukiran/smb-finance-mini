package com.smbfinance

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
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
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginButton: Button
    lateinit var registerLink: TextView
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sharedPreferences = getSharedPreferences("SMBFinance", MODE_PRIVATE)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.btnLogin)
        registerLink = findViewById(R.id.register_link)
        progressBar = findViewById(R.id.progressBar)
        
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (isNetworkAvailable()) {
                showLoading()
                performLogin(username, password)
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading() {
        progressBar.visibility = ProgressBar.VISIBLE
        loginButton.isEnabled = false
        usernameInput.isEnabled = false
        passwordInput.isEnabled = false
    }

    private fun hideLoading() {
        progressBar.visibility = ProgressBar.GONE
        loginButton.isEnabled = true
        usernameInput.isEnabled = true
        passwordInput.isEnabled = true
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
                val response = RetrofitClient.apiService.login(loginRequest)
                val responseBody = response.body()
                val errorBody = response.errorBody()?.string()

                withContext(Dispatchers.Main) {
                    hideLoading()
                    if (response.isSuccessful && responseBody != null) {
                        Log.d(TAG, "Login response: ${gson.toJson(responseBody)}")
                        
                        if (responseBody.status == "success") {
                            // Store the auth token from the response header
                            val authToken = response.headers()["Authorization"]
                            if (authToken != null) {
                                RetrofitClient.setAuthToken(authToken)
                                Log.d(TAG, "Auth token stored: $authToken")
                            }
                            
                            // Store user details
                            with(sharedPreferences.edit()) {
                                putString("user_fullName", "${responseBody.firstname} ${responseBody.lastname}")
                                putString("user_email", responseBody.email)
                                putString("user_role", responseBody.role)
                                apply()
                            }
                            Log.d(TAG, "Stored user details - Name: ${responseBody.firstname} ${responseBody.lastname}, Email: ${responseBody.email}, Role: ${responseBody.role}")
                            
                            // Clear input fields
                            usernameInput.text.clear()
                            passwordInput.text.clear()
                            
                            // Navigate to dashboard
                            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            val errorMessage = responseBody.message ?: "Invalid response format"
                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(this@MainActivity, "Cannot connect to server. Please check your internet connection.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(this@MainActivity, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show()
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