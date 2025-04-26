package com.smbfinance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.Customer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCustomerActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var etInterest: TextInputEditText
    private lateinit var etDuration: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etAmount = findViewById(R.id.etAmount)
        etInterest = findViewById(R.id.etInterest)
        etDuration = findViewById(R.id.etDuration)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Customer"

        // Setup save button click listener
        findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            saveCustomer()
        }
    }

    private fun saveCustomer() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val amount = etAmount.text.toString().trim()
        val interest = etInterest.text.toString().trim()
        val duration = etDuration.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || 
            amount.isEmpty() || interest.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val customer = Customer(
            name = name,
            phone = phone,
            address = address,
            amount = amount.toDouble(),
            interest = interest.toDouble(),
            duration = duration.toInt()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.addCustomer(customer)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddCustomerActivity, "Customer added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddCustomerActivity, "Failed to add customer", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddCustomerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 