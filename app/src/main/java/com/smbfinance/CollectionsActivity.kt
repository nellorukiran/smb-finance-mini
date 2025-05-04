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
import com.smbfinance.adapter.CollectionsAdapter
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.CollectionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.text.NumberFormat

class CollectionsActivity : AppCompatActivity() {
    private val TAG = "CollectionsActivity"
    private lateinit var toolbar: Toolbar
    private lateinit var fromDateInput: TextInputEditText
    private lateinit var toDateInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var collectionsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CollectionsAdapter
    private lateinit var summaryContainer: View
    private lateinit var totalCollections: TextView
    private lateinit var totalAmount: TextView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        fromDateInput = findViewById(R.id.fromDateInput)
        toDateInput = findViewById(R.id.toDateInput)
        searchButton = findViewById(R.id.searchButton)
        collectionsRecyclerView = findViewById(R.id.collectionsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        summaryContainer = findViewById(R.id.summaryContainer)
        totalCollections = findViewById(R.id.totalCollections)
        totalAmount = findViewById(R.id.totalAmount)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Collections"

        // Setup RecyclerView
        collectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CollectionsAdapter(emptyList())
        collectionsRecyclerView.adapter = adapter

        // Setup date pickers
        fromDateInput.setOnClickListener { showDatePicker(fromDateInput) }
        toDateInput.setOnClickListener { showDatePicker(toDateInput) }

        // Setup search button
        setupSearchButton()
    }

    private fun showDatePicker(input: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)
                input.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun setupSearchButton() {
        searchButton.setOnClickListener {
            val fromDate = fromDateInput.text.toString()
            val toDate = toDateInput.text.toString()

            if (fromDate.isEmpty() || toDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Search clicked with dates: from=$fromDate, to=$toDate")
            fetchCollections(fromDate, toDate)
        }
    }

    private fun fetchCollections(fromDate: String, toDate: String) {
        progressBar.visibility = View.VISIBLE
        collectionsRecyclerView.visibility = View.GONE
        summaryContainer.visibility = View.GONE

        Log.d(TAG, "Fetching collections from $fromDate to $toDate")
        Log.d(TAG, "Base URL: ${RetrofitClient.getBaseUrl()}")
        Log.d(TAG, "Auth Token: ${RetrofitClient.getAuthToken()?.take(10)}...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCollections(fromDate, toDate)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    collectionsRecyclerView.visibility = View.VISIBLE

                    if (response.isSuccessful) {
                        val collectionResponse = response.body()
                        if (collectionResponse != null) {
                            if (collectionResponse.status == "success") {
                                Log.d(TAG, "Successfully fetched ${collectionResponse.collections.size} collections")
                                adapter = CollectionsAdapter(collectionResponse.collections)
                                collectionsRecyclerView.adapter = adapter
                                
                                // Update summary
                                summaryContainer.visibility = View.VISIBLE
                                totalCollections.text = collectionResponse.totalCollections.toString()
                                totalAmount.text = numberFormat.format(collectionResponse.totalAmount)
                            } else {
                                Log.e(TAG, "API returned error status: ${collectionResponse.status}")
                                Toast.makeText(this@CollectionsActivity, "Error: ${collectionResponse.status}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e(TAG, "Response body is null")
                            Toast.makeText(this@CollectionsActivity, "No data received from server", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Failed to fetch collections. Code: ${response.code()}, Error: $errorBody")
                        Log.e(TAG, "Request URL: ${response.raw().request.url}")
                        Log.e(TAG, "Request Headers: ${response.raw().request.headers}")
                        
                        when (response.code()) {
                            401 -> Toast.makeText(this@CollectionsActivity, "Authentication failed. Please login again.", Toast.LENGTH_SHORT).show()
                            404 -> Toast.makeText(this@CollectionsActivity, "API endpoint not found", Toast.LENGTH_SHORT).show()
                            500 -> Toast.makeText(this@CollectionsActivity, "Server error. Please try again later.", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(this@CollectionsActivity, "Failed to fetch collections. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching collections", e)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    collectionsRecyclerView.visibility = View.VISIBLE
                    Toast.makeText(this@CollectionsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 