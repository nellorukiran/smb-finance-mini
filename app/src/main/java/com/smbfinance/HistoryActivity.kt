package com.smbfinance

import android.content.Intent
import android.os.Bundle
import android.util.Log
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