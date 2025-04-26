package com.smbfinance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import com.smbfinance.adapter.TransactionAdapter
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.PaymentHistoryResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private val TAG = "HistoryActivity"
    private lateinit var etCustomerId: TextInputEditText
    private lateinit var btnSearch: MaterialButton
    private lateinit var cardCustomerDetails: MaterialCardView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNextDueAmount: TextView
    private lateinit var tvTransactionHistoryTitle: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)

        // Initialize views
        etCustomerId = findViewById(R.id.etCustomerId)
        btnSearch = findViewById(R.id.btnSearch)
        cardCustomerDetails = findViewById(R.id.cardCustomerDetails)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvAddress = findViewById(R.id.tvAddress)
        tvNextDueAmount = findViewById(R.id.tvNextDueAmount)
        tvTransactionHistoryTitle = findViewById(R.id.tvTransactionHistoryTitle)
        rvTransactions = findViewById(R.id.rvTransactions)

        // Setup RecyclerView
        rvTransactions.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList())
        rvTransactions.adapter = transactionAdapter

        btnSearch.setOnClickListener {
            val customerId = etCustomerId.text.toString().trim()
            if (customerId.isEmpty()) {
                Toast.makeText(this, "Please enter Customer ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchPaymentHistory(customerId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
                true
            }
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

    private fun performLogout() {
        // Clear the auth token first
        RetrofitClient.setAuthToken(null)
        
        // Navigate to login screen immediately
        val intent = Intent(this@HistoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        
        // Optionally make the API call in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Attempting logout...")
                val response = RetrofitClient.apiService.logout()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Logout successful")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Logout failed with code: ${response.code()}, error: $errorBody")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
            }
        }
    }

    private fun searchPaymentHistory(customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getPaymentHistory(customerId)
                Log.e(TAG, "response: ${response}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val paymentHistory = response.body()!!
                        displayPaymentHistory(paymentHistory)
                    } else {
                        Toast.makeText(this@HistoryActivity, "Failed to fetch payment history", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HistoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayPaymentHistory(paymentHistory: PaymentHistoryResponse) {
        // Display customer details
        val customerDetails = paymentHistory.customerDetails
        tvCustomerName.text = customerDetails.customerName
        tvPhoneNumber.text = "Phone: ${customerDetails.phoneNumber}"
        tvAddress.text = "Address: ${customerDetails.address}"
        tvNextDueAmount.text = "Bal Due Amount: ₹${customerDetails.totalDueAmount}"
        cardCustomerDetails.visibility = View.VISIBLE

        // Display transactions
        transactionAdapter = TransactionAdapter(paymentHistory.transactions)
        rvTransactions.adapter = transactionAdapter
        tvTransactionHistoryTitle.visibility = View.VISIBLE
        rvTransactions.visibility = View.VISIBLE
    }
} 