package com.smbfinance

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.smbfinance.adapter.FilesAdapter
import com.smbfinance.api.ApiService
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.FilesResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilesActivity : AppCompatActivity() {
    private lateinit var fromDateInput: TextInputEditText
    private lateinit var toDateInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var filesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var totalFiles: TextView
    private lateinit var totalDueAmount: TextView
    private lateinit var totalProfit: TextView
    private lateinit var dueAmount: TextView
    private lateinit var totalInterestAmount: TextView
    private lateinit var totalDocCharges: TextView
    private lateinit var profit: TextView
    private lateinit var summaryContainer: View

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val TAG = "FilesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Files"

        initializeViews()
        setupRecyclerView()
        setupDatePickers()
        setupSearchButton()
    }

    private fun initializeViews() {
        fromDateInput = findViewById(R.id.fromDateInput)
        toDateInput = findViewById(R.id.toDateInput)
        searchButton = findViewById(R.id.searchButton)
        filesRecyclerView = findViewById(R.id.filesRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        totalFiles = findViewById(R.id.totalFiles)
        totalDueAmount = findViewById(R.id.totalDueAmount)
        totalProfit = findViewById(R.id.totalProfit)
        dueAmount = findViewById(R.id.dueAmount)
        totalInterestAmount = findViewById(R.id.totalInterestAmount)
        totalDocCharges = findViewById(R.id.totalDocCharges)
        profit = findViewById(R.id.profit)
        summaryContainer = findViewById(R.id.summaryContainer)
    }

    private fun setupRecyclerView() {
        filesRecyclerView.layoutManager = LinearLayoutManager(this)
        filesRecyclerView.adapter = FilesAdapter(emptyList())
    }

    private fun setupDatePickers() {
        fromDateInput.setOnClickListener { showDatePicker(fromDateInput) }
        toDateInput.setOnClickListener { showDatePicker(toDateInput) }
    }

    private fun showDatePicker(input: TextInputEditText) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                input.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setupSearchButton() {
        searchButton.setOnClickListener {
            val fromDate = fromDateInput.text.toString()
            val toDate = toDateInput.text.toString()

            if (fromDate.isEmpty() || toDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fetchFiles(fromDate, toDate)
        }
    }

    private fun fetchFiles(fromDate: String, toDate: String) {
        progressBar.visibility = View.VISIBLE
        summaryContainer.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Fetching files from $fromDate to $toDate")
                Log.d(TAG, "Base URL: ${RetrofitClient.getBaseUrl()}")
                Log.d(TAG, "Auth Token: ${RetrofitClient.getAuthToken()}")

                val response = RetrofitClient.apiService.getFiles(
                    startDate = fromDate,
                    endDate = toDate
                )
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    
                    if (response.isSuccessful && response.body() != null) {
                        val filesResponse = response.body()!!
                        Log.d(TAG, "Total files received: ${filesResponse.files.size}")
                        Log.d(TAG, "Total files count: ${filesResponse.totalFiles}")
                        Log.d(TAG, "Total due amount: ${filesResponse.totalDueAmount}")
                        Log.d(TAG, "Total profit: ${filesResponse.totalProfit}")
                        updateUI(filesResponse)
                    } else {
                        val errorMessage = when (response.code()) {
                            401 -> "Authentication failed. Please login again."
                            404 -> "No files found for the selected date range."
                            500 -> "Server error. Please try again later."
                            else -> "Failed to fetch files. Please try again."
                        }
                        Log.e(TAG, "Error fetching files: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@FilesActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Log.e(TAG, "Exception while fetching files", e)
                    Toast.makeText(this@FilesActivity, "Failed to fetch files. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(filesResponse: FilesResponse) {
        summaryContainer.visibility = View.VISIBLE
        
        // Update summary information
        totalFiles.text = filesResponse.totalFiles.toString()
        dueAmount.text = numberFormat.format(filesResponse.dueAmount)
        totalDueAmount.text = numberFormat.format(filesResponse.totalDueAmount)
        totalInterestAmount.text = numberFormat.format(filesResponse.totalInterestAmount)
        totalDocCharges.text = numberFormat.format(filesResponse.totalDocCharges)
        profit.text = numberFormat.format(filesResponse.profit)
        totalProfit.text = numberFormat.format(filesResponse.totalProfit)

        // Log additional information for debugging
        Log.d(TAG, "Total Doc Charges: ${numberFormat.format(filesResponse.totalDocCharges)}")
        Log.d(TAG, "Total Interest Amount: ${numberFormat.format(filesResponse.totalInterestAmount)}")
        Log.d(TAG, "Due Amount: ${numberFormat.format(filesResponse.dueAmount)}")
        Log.d(TAG, "Profit: ${numberFormat.format(filesResponse.profit)}")
        Log.d(TAG, "Status: ${filesResponse.status}")

        // Update RecyclerView with files list
        if (filesResponse.files.isNotEmpty()) {
            Log.d(TAG, "Setting adapter with ${filesResponse.files.size} files")
            filesRecyclerView.adapter = FilesAdapter(filesResponse.files)
        } else {
            Log.d(TAG, "No files to display")
            Toast.makeText(this, "No files found for the selected date range", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 