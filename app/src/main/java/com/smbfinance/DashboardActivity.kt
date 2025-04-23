package com.smbfinance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.smbfinance.adapter.DashboardAdapter
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.DashboardItem
import com.smbfinance.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "DashboardActivity"
    private lateinit var dashboardGrid: RecyclerView
    private lateinit var adapter: DashboardAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var currentUser: User
    private lateinit var tvActiveCustomers: TextView
    private lateinit var tvHomeApplianceCustomers: TextView
    private lateinit var tvLoanCustomers: TextView
    private lateinit var tvWelcome: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        tvActiveCustomers = findViewById(R.id.tvActiveCustomers)
        tvHomeApplianceCustomers = findViewById(R.id.tvHomeApplianceCustomers)
        tvLoanCustomers = findViewById(R.id.tvLoanCustomers)
        tvWelcome = findViewById(R.id.tvWelcome)

        // Get user data from intent
        currentUser = intent.getSerializableExtra("user") as User
        tvWelcome.text = "Welcome, ${currentUser.fullName}!"

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Setup dashboard grid
        dashboardGrid = findViewById(R.id.dashboardGrid)
        setupDashboard()

        // Update profile info in navigation header
        updateProfileInfo()

        // Fetch customer stats
        fetchCustomerStats()
    }

    private fun updateProfileInfo() {
        val headerView = navigationView.getHeaderView(0)
        val profileName = headerView.findViewById<TextView>(R.id.profile_name)
        val profileEmail = headerView.findViewById<TextView>(R.id.profile_email)

        profileName.text = currentUser.fullName
        profileEmail.text = currentUser.email
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

        adapter = DashboardAdapter(items)
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
                        tvActiveCustomers.text = stats.activeCustomers.toString()
                        tvHomeApplianceCustomers.text = stats.homeApplianceCustomers.toString()
                        tvLoanCustomers.text = stats.loanCustomers.toString()
                    } else {
                        Log.e(TAG, "Failed to fetch customer stats: ${response.code()}")
                        Toast.makeText(this@DashboardActivity, "Failed to load customer statistics", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Error fetching customer stats", e)
                    Toast.makeText(this@DashboardActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                // Already on dashboard
            }
            R.id.nav_add -> {
                // TODO: Navigate to add customer page
            }
            R.id.nav_update -> {
                // TODO: Navigate to update customer page
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_view -> {
                // TODO: Navigate to customer view page
            }
            R.id.nav_settings -> {
                // TODO: Navigate to settings
            }
            R.id.nav_logout -> {
                // Navigate back to login
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
} 