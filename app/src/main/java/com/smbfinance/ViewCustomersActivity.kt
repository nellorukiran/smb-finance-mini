package com.smbfinance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smbfinance.adapter.CustomerAdapter
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.Customer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewCustomersActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_customers)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "View Customers"

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CustomerAdapter { customer ->
            // TODO: Handle customer item click
        }
        recyclerView.adapter = adapter

        // Load customers
        loadCustomers()
    }

    private fun loadCustomers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCustomers()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val customers = response.body()!!.data
                        adapter.submitList(customers)
                    } else {
                        Toast.makeText(this@ViewCustomersActivity, "Failed to load customers", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewCustomersActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 