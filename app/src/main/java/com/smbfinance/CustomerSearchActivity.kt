package com.smbfinance

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.smbfinance.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerSearchActivity : AppCompatActivity() {
    private lateinit var etCustomerId: TextInputEditText
    private lateinit var btnSearch: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_search)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)

        // Initialize views
        etCustomerId = findViewById(R.id.etCustomerId)
        btnSearch = findViewById(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val customerId = etCustomerId.text.toString().trim()
            if (customerId.isEmpty()) {
                Toast.makeText(this, "Please enter Customer ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchCustomer(customerId)
        }
    }

    private fun searchCustomer(customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCustomerById(customerId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CustomerSearchActivity, "Customer found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CustomerSearchActivity, "Customer not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CustomerSearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 