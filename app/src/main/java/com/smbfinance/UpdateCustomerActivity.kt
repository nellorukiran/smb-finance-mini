package com.smbfinance

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.PaymentUpdateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateCustomerActivity : AppCompatActivity() {
    private lateinit var etCustomerId: EditText
    private lateinit var btnSearch: MaterialButton
    private lateinit var detailsLayout: MaterialCardView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvProductName: TextView
    private lateinit var tvTotalDues: TextView
    private lateinit var tvPerMonthDue: TextView
    private lateinit var tvTotalDueAmount: TextView
    private lateinit var tvNextDueAmount: TextView
    private lateinit var etPaidAmount: TextInputEditText
    private lateinit var etPaidDate: TextInputEditText
    private lateinit var etPenalty: TextInputEditText
    private lateinit var btnUpdate: MaterialButton

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_customer)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)

        // Initialize views
        etCustomerId = findViewById(R.id.etCustomerId)
        btnSearch = findViewById(R.id.btnSearch)
        detailsLayout = findViewById(R.id.detailsLayout)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvAddress = findViewById(R.id.tvAddress)
        tvProductName = findViewById(R.id.tvProductName)
        tvTotalDues = findViewById(R.id.tvTotalDues)
        tvPerMonthDue = findViewById(R.id.tvPerMonthDue)
        tvTotalDueAmount = findViewById(R.id.tvTotalDueAmount)
        tvNextDueAmount = findViewById(R.id.tvNextDueAmount)
        etPaidAmount = findViewById(R.id.etPaidAmount)
        etPaidDate = findViewById(R.id.etPaidDate)
        etPenalty = findViewById(R.id.etPenalty)
        btnUpdate = findViewById(R.id.btnUpdate)

        // Setup date picker
        etPaidDate.setOnClickListener {
            showDatePicker()
        }

        // Setup search button
        btnSearch.setOnClickListener {
            val customerId = etCustomerId.text.toString().trim()
            if (customerId.isEmpty()) {
                Toast.makeText(this, "Please enter Customer ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchCustomer(customerId)
        }

        // Setup update button
        btnUpdate.setOnClickListener {
            updatePayment()
        }
    }

    private fun searchCustomer(customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCustomerById(customerId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        detailsLayout.visibility = View.VISIBLE
                        // Set current date as paid date
                        etPaidDate.setText(dateFormat.format(Calendar.getInstance().time))
                    } else {
                        Toast.makeText(this@UpdateCustomerActivity, "Customer not found", Toast.LENGTH_SHORT).show()
                        detailsLayout.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UpdateCustomerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    detailsLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                etPaidDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updatePayment() {
        val customerId = etCustomerId.text.toString().trim()
        val paidAmount = etPaidAmount.text.toString().trim()
        val paidDate = etPaidDate.text.toString().trim()
        val penalty = etPenalty.text.toString().trim()

        if (customerId.isEmpty() || paidAmount.isEmpty() || paidDate.isEmpty() || penalty.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val request = PaymentUpdateRequest(
            customerId = customerId,
            paidAmount = paidAmount.toDouble(),
            paidDate = paidDate,
            penalty = penalty.toDouble()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.updatePayment(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UpdateCustomerActivity, "Payment updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@UpdateCustomerActivity, "Failed to update payment", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UpdateCustomerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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