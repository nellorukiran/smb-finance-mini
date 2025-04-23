package com.smbfinance

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.RegisterRequest
import com.smbfinance.model.RegisterResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var etUserType: AutoCompleteTextView
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etUserType = findViewById(R.id.etUserType)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        // Setup user type dropdown
        val userTypes = arrayOf("Financial Advisor", "Customer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, userTypes)
        etUserType.setAdapter(adapter)

        btnRegister.setOnClickListener {
            if (validateInputs()) {
                performRegistration()
            }
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun performRegistration() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val userType = etUserType.text.toString().trim()

        // Convert user type to API format
        val apiUserType = if (userType == "Financial Advisor") "ADVISOR" else "USER"

        val registerRequest = RegisterRequest(
            firstName,
            lastName,
            username,
            email,
            phoneNumber,
            password,
            password, // Using the same password for confirmPassword
            apiUserType
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.register(registerRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val registerResponse = response.body()!!
                        if (registerResponse.isSuccess()) {
                            Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val userType = etUserType.text.toString().trim()

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.error = "First name is required"
            return false
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.error = "Last name is required"
            return false
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.error = "Username is required"
            return false
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email address"
            return false
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.error = "Phone number is required"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }

        if (TextUtils.isEmpty(userType)) {
            etUserType.error = "User type is required"
            return false
        }

        return true
    }
} 