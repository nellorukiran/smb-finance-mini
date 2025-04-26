package com.smbfinance

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CustomerDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_details)

        // Get customer details from intent
        val customerId = intent.getStringExtra("customerId") ?: ""
        val customerName = intent.getStringExtra("customerName") ?: ""
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val productName = intent.getStringExtra("productName") ?: ""
        val totalDues = intent.getIntExtra("totalDues", 0)
        val perMonthDue = intent.getDoubleExtra("perMonthDue", 0.0)
        val penalty = intent.getDoubleExtra("penalty", 0.0)
        val purchaseDate = intent.getStringExtra("purchaseDate") ?: ""
        val dueTime = intent.getStringExtra("dueTime") ?: ""
        val totalDueAmount = intent.getDoubleExtra("totalDueAmount", 0.0)
        val nextDueAmount = intent.getDoubleExtra("nextDueAmount", 0.0)
        val custStatus = intent.getStringExtra("custStatus") ?: ""

        // Initialize TextViews
        val tvCustomerName: TextView = findViewById(R.id.tvCustomerName)
        val tvCustomerId: TextView = findViewById(R.id.tvCustomerId)
        val tvPhoneNumber: TextView = findViewById(R.id.tvPhoneNumber)
        val tvAddress: TextView = findViewById(R.id.tvAddress)
        val tvProductName: TextView = findViewById(R.id.tvProductName)
        val tvPurchaseDate: TextView = findViewById(R.id.tvPurchaseDate)
        val tvDueTime: TextView = findViewById(R.id.tvDueTime)
        val tvTotalDues: TextView = findViewById(R.id.tvTotalDues)
        val tvPerMonthDue: TextView = findViewById(R.id.tvPerMonthDue)
        val tvPenalty: TextView = findViewById(R.id.tvPenalty)
        val tvTotalDueAmount: TextView = findViewById(R.id.tvTotalDueAmount)
        val tvNextDueAmount: TextView = findViewById(R.id.tvNextDueAmount)
        val tvCustStatus: TextView = findViewById(R.id.tvCustStatus)

        // Set customer information
        tvCustomerName.text = customerName
        tvCustomerId.text = "Customer ID: $customerId"
        tvPhoneNumber.text = "Phone: $phoneNumber"
        tvAddress.text = "Address: $address"
        tvProductName.text = productName
        tvPurchaseDate.text = "Purchase Date: $purchaseDate"
        tvDueTime.text = "Due Time: $dueTime"
        tvTotalDues.text = "Total Dues: $totalDues"
        tvPerMonthDue.text = "Monthly Due: $perMonthDue"
        tvPenalty.text = "Penalty: $penalty"
        tvTotalDueAmount.text = "Total Due Amount: $totalDueAmount"
        tvNextDueAmount.text = "Next Due Amount: $nextDueAmount"
        tvCustStatus.text = "Status: $custStatus"
    }
} 