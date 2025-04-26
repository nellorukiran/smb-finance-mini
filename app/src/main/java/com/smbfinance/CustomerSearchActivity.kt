package com.smbfinance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.smbfinance.api.RetrofitClient
import com.smbfinance.model.CustomerDetailsResponse
import com.smbfinance.model.CustomerDetailsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerSearchActivity : AppCompatActivity() {
    private val TAG = "CustomerSearchActivity"
    private lateinit var etCustomerId: TextInputEditText
    private lateinit var btnSearch: MaterialButton
    private lateinit var detailsCard: CardView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvShopName: TextView
    private lateinit var tvProductName: TextView
    private lateinit var tvProductModel: TextView
    private lateinit var tvPurchaseDateStr: TextView
    private lateinit var tvActualPrice: TextView
    private lateinit var tvSalePrice: TextView
    private lateinit var tvTotalDues: TextView
    private lateinit var tvAdvance: TextView
    private lateinit var tvPenalty: TextView
    private lateinit var tvDueTime: TextView
    private lateinit var tvDueAmount: TextView
    private lateinit var tvInterestAmount: TextView
    private lateinit var tvTotalDueAmount: TextView
    private lateinit var tvPerMonthDue: TextView
    private lateinit var tvProfit: TextView
    private lateinit var tvDocCharges: TextView
    private lateinit var tvTotalProfit: TextView
    private lateinit var tvCustStatus: TextView
    private lateinit var tvAadharNumber: TextView
    private lateinit var tvCreateDate: TextView
    private lateinit var tvCreatedBy: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_search)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)

        // Initialize views
        etCustomerId = findViewById(R.id.etCustomerId)
        btnSearch = findViewById(R.id.btnSearch)
        detailsCard = findViewById(R.id.detailsCard)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvPurchaseDateStr = findViewById(R.id.tvPurchaseDateStr)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvAddress = findViewById(R.id.tvAddress)
        tvProductName = findViewById(R.id.tvProductName)
        tvTotalDues = findViewById(R.id.tvTotalDues)
        tvPerMonthDue = findViewById(R.id.tvPerMonthDue)
        tvTotalDueAmount = findViewById(R.id.tvTotalDueAmount)
        tvInterestAmount = findViewById(R.id.tvInterestAmount)
        tvShopName = findViewById(R.id.tvShopName)
        tvProductModel = findViewById(R.id.tvProductModel)
        tvActualPrice = findViewById(R.id.tvActualPrice)
        tvSalePrice = findViewById(R.id.tvSalePrice)
        tvAdvance = findViewById(R.id.tvAdvance)
        tvPenalty = findViewById(R.id.tvPenalty)
        tvDueTime = findViewById(R.id.tvDueTime)
        tvDueAmount = findViewById(R.id.tvDueAmount)
        tvProfit = findViewById(R.id.tvProfit)
        tvDocCharges = findViewById(R.id.tvDocCharges)
        tvTotalProfit = findViewById(R.id.tvTotalProfit)
        tvCustStatus = findViewById(R.id.tvCustStatus)
        tvAadharNumber = findViewById(R.id.tvAadharNumber)
        tvCreateDate = findViewById(R.id.tvCreateDate)
        tvCreatedBy = findViewById(R.id.tvCreatedBy)

        // Setup search button
        btnSearch.setOnClickListener {
            val customerId = etCustomerId.text.toString().trim()
            if (customerId.isEmpty()) {
                Toast.makeText(this, "Please enter Customer ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchCustomer(customerId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
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

    private fun searchCustomer(customerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCustomerDetails(customerId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val customerDetails = response.body()!!.data!!
                        displayCustomerDetails(customerDetails)
                    } else {
                        Toast.makeText(this@CustomerSearchActivity, "Customer not found", Toast.LENGTH_SHORT).show()
                        detailsCard.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CustomerSearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    detailsCard.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayCustomerDetails(details: CustomerDetailsData) {
        detailsCard.visibility = View.VISIBLE
        tvCustomerName.text = "Name: ${details.customerName}"
        tvPhoneNumber.text = "Phone: ${details.phoneNumber}"
        tvAddress.text = "Address: ${details.address}"
        tvShopName.text = "Shop Name: ${details.shopName}"
        tvProductName.text = "Product Name & Model: ${details.productName} (${details.productModel})"
        tvPurchaseDateStr.text = "Date Of Purchase: ${details.purchaseDateStr}"
        tvActualPrice.text = "Actual Price: ${details.actualPrice}"
        tvSalePrice.text = "Saled Price: ${details.salePrice}"
        tvAdvance.text = "Advance: ${details.advance}"
        tvPenalty.text = "Any Penalty ?: ${details.penalty}"
        tvDueAmount.text = "Due Amount: ${details.dueAmount}"
        tvDueTime.text = "Due Time: ${details.dueTime}"
        tvInterestAmount.text = "Interest Amount: ₹${details.interestAmount}"
        tvTotalDueAmount.text = "Due Amount: ₹${details.totalDueAmount}"
        tvTotalDues.text = "No Of Dues: ${details.totalDues}"
        tvPerMonthDue.text = "Monthly Due: ₹${details.perMonthDue}"
        tvProfit.text = "Profit: ${details.profit}"
        tvDocCharges.text = "Document Charges: ${details.docCharges}"
        tvTotalProfit.text = "Total Profit: ${details.totalProfit}"
        tvCustStatus.text = "Customer Status: ${details.custStatus}"
        if(details.aadharNumber != null) {
            tvAadharNumber.text = "Aadhar Number: ${details.aadharNumber}"
        }
        if(details.createdBy != null) {
            tvCreatedBy.text = "Created By: ${details.createdBy}"
        }
        if(details.createdDate != null) {
            tvCreateDate.text = "Created Date: ${details.createdDate}"
        }
    }
} 