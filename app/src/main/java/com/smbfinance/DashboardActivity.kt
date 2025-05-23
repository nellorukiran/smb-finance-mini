package com.smbfinance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.smbfinance.adapter.DashboardAdapter
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.CustomerStatsData
import com.smbfinance.model.DashboardItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {
    private val TAG = "DashboardActivity"
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var dashboardGrid: RecyclerView
    private lateinit var adapter: DashboardAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var tvWelcome: TextView
    private lateinit var tvActiveCustomers: TextView
    private lateinit var tvHomeApplianceCustomers: TextView
    private lateinit var tvLoanCustomers: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        tvWelcome = findViewById(R.id.tvWelcome)
        tvActiveCustomers = findViewById(R.id.tvActiveCustomers)
        tvHomeApplianceCustomers = findViewById(R.id.tvHomeApplianceCustomers)
        tvLoanCustomers = findViewById(R.id.tvLoanCustomers)

        // Set welcome message
        tvWelcome.text = "Welcome to SMB Finance!"

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Setup navigation view
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    // Already on dashboard, do nothing
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddCustomerActivity::class.java))
                }
                R.id.nav_update -> {
                    startActivity(Intent(this, UpdateCustomerActivity::class.java))
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }
                R.id.nav_view -> {
                    startActivity(Intent(this, ViewCustomersActivity::class.java))
                }
                R.id.nav_settings -> {
                    // TODO: Implement settings activity
                }
                R.id.nav_logout -> {
                    performLogout()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        setupDashboard()
        fetchCustomerStats()
    }

    private fun performLogout() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Attempting logout...")
                val response = RetrofitClient.apiService.logout()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Logout successful")
                        // Clear any stored user data or tokens here if needed
                        val intent = Intent(this@DashboardActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Logout failed with code: ${response.code()}, error: $errorBody")
                        Toast.makeText(
                            this@DashboardActivity,
                            "Logout failed: ${errorBody ?: "Unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Error during logout", e)
                    val errorMessage = when (e) {
                        is java.net.UnknownHostException -> "No internet connection"
                        is java.net.SocketTimeoutException -> "Connection timeout"
                        else -> e.message ?: "Unknown error occurred"
                    }
                    Toast.makeText(this@DashboardActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupDashboard() {
        val items = listOf(
            DashboardItem(
                "Add",
                "Add new customers, manage customer details, and view customer history",
                R.drawable.icon_account_circle
            ),
            DashboardItem(
                "Update",
                "Update customer information, manage customer accounts, and view account details",
                R.drawable.icon_account_circle
            ),
            DashboardItem(
                "History",
                "View customer history, manage customer transactions, and view transaction details",
                R.drawable.icon_account_circle
            ),
            DashboardItem(
                "View",
                "View customer accounts, manage customer transactions, and view transaction details",
                R.drawable.icon_account_circle
            )
        )

        adapter = DashboardAdapter(items) { item ->
            when (item.title) {
                "Add" -> startActivity(Intent(this, AddCustomerActivity::class.java))
                "Update" -> startActivity(Intent(this, UpdateCustomerActivity::class.java))
                "History" -> startActivity(Intent(this, HistoryActivity::class.java))
                "View" -> startActivity(Intent(this, ViewCustomersActivity::class.java))
            }
        }

        dashboardGrid = findViewById(R.id.dashboardGrid)
        dashboardGrid.layoutManager = GridLayoutManager(this, 2)
        dashboardGrid.adapter = adapter
    }

    private fun fetchCustomerStats() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCustomerStats()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val stats = response.body()!!.data
                        updateStatsUI(stats)
                    } else {
                        Log.e(TAG, "Failed to fetch customer stats: ${response.code()}")
                        Toast.makeText(this@DashboardActivity, "Failed to load customer stats", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching customer stats", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateStatsUI(stats: CustomerStatsData) {
        tvActiveCustomers.text = stats.activeCustomers.toString()
        tvHomeApplianceCustomers.text = stats.homeApplianceCustomers.toString()
        tvLoanCustomers.text = stats.loanCustomers.toString()
    }
} 