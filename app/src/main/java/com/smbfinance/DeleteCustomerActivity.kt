package com.smbfinance

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.DeleteSearchResponse
import com.smbfinance.model.DeleteCustomerDetails
import com.smbfinance.model.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class DeleteCustomerActivity : AppCompatActivity() {
    private val TAG = "DeleteCustomerActivity"
    private val gson = Gson()
    private lateinit var etCustomerId: TextInputEditText
    private lateinit var customerIdLayout: TextInputLayout
    private lateinit var btnSearch: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var btnHome: ImageButton
    private lateinit var tvCustomerName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvProductName: TextView
    private lateinit var tvTotalDues: TextView
    private lateinit var tvPerMonthDue: TextView
    private lateinit var tvTotalDueAmount: TextView
    private lateinit var tvNextDueAmount: TextView
    private lateinit var customerDetailsCard: MaterialCardView
    private var currentCustomerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_customer)

        // Initialize views
        etCustomerId = findViewById(R.id.etCustomerId)
        customerIdLayout = findViewById(R.id.customerIdLayout)
        btnSearch = findViewById(R.id.btnSearch)
        btnDelete = findViewById(R.id.btnDelete)
        btnHome = findViewById(R.id.btnHome)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvAddress = findViewById(R.id.tvAddress)
        tvProductName = findViewById(R.id.tvProductName)
        tvTotalDues = findViewById(R.id.tvTotalDues)
        tvPerMonthDue = findViewById(R.id.tvPerMonthDue)
        tvTotalDueAmount = findViewById(R.id.tvTotalDueAmount)
        tvNextDueAmount = findViewById(R.id.tvNextDueAmount)
        customerDetailsCard = findViewById(R.id.customerDetailsCard)

        btnHome.setOnClickListener {
            finish()
        }

        btnSearch.setOnClickListener {
            val customerId = etCustomerId.text.toString().trim()
            if (customerId.isNotEmpty()) {
                searchCustomer(customerId)
            } else {
                customerIdLayout.error = "Please enter Customer ID"
            }
        }

        btnDelete.setOnClickListener {
            currentCustomerId?.let { customerId ->
                showDeleteConfirmationDialog(customerId)
            }
        }
    }

    private fun showDeleteConfirmationDialog(customerId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this customer? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteCustomer(customerId)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun searchCustomer(customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.deleteSearchCustomer(customerId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val deleteSearchResponse = response.body()
                        if (deleteSearchResponse != null && deleteSearchResponse.customer != null) {
                            displayCustomerDetails(deleteSearchResponse.customer)
                            currentCustomerId = customerId
                        } else {
                            Toast.makeText(this@DeleteCustomerActivity, "Customer not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMessage = try {
                            val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Error searching for customer"
                        }
                        Toast.makeText(this@DeleteCustomerActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Error searching customer", e)
                    Toast.makeText(this@DeleteCustomerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayCustomerDetails(customerDetails: DeleteCustomerDetails) {
        // Basic Information
        tvCustomerName.text = "Name: ${customerDetails.customerName}"
        tvPhoneNumber.text = "Phone: ${customerDetails.phoneNumber}"
        tvAddress.text = "Address: ${customerDetails.address}"
        tvProductName.text = "Product: ${customerDetails.productName}"

        // Due Information
        tvTotalDues.text = "Total Dues: ${customerDetails.totalDues}"
        tvPerMonthDue.text = "Monthly Due: ₹${String.format("%.2f", customerDetails.perMonthDue)}"
        tvTotalDueAmount.text = "Total Due Amount: ₹${String.format("%.2f", customerDetails.totalDueAmount)}"
        tvNextDueAmount.text = "Next Due Amount: ₹${String.format("%.2f", customerDetails.nextDueAmount)}"

        // Show the details card
        customerDetailsCard.visibility = View.VISIBLE
    }

    private fun deleteCustomer(customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.deleteCustomer(customerId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Toast.makeText(this@DeleteCustomerActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            // Clear the form and hide details card
                            etCustomerId.text?.clear()
                            customerDetailsCard.visibility = View.GONE
                            currentCustomerId = null
                        } else {
                            Toast.makeText(this@DeleteCustomerActivity, "Customer deleted successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        val errorMessage = try {
                            val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Error deleting customer"
                        }
                        Toast.makeText(this@DeleteCustomerActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Error deleting customer", e)
                    Toast.makeText(this@DeleteCustomerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
} 