package com.smbfinance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smbfinance.R
import com.smbfinance.model.File
import java.text.NumberFormat
import java.util.Locale

class FilesAdapter(private val files: List<File>) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val customerName: TextView = view.findViewById(R.id.customerName)
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber)
        val address: TextView = view.findViewById(R.id.address)
        val productName: TextView = view.findViewById(R.id.productName)
        val productModel: TextView = view.findViewById(R.id.productModel)
        val purchaseDate: TextView = view.findViewById(R.id.purchaseDate)
        val dueTime: TextView = view.findViewById(R.id.dueTime)
        val dueAmount: TextView = view.findViewById(R.id.dueAmount)
        val totalDueAmount: TextView = view.findViewById(R.id.totalDueAmount)
        val perMonthDue: TextView = view.findViewById(R.id.perMonthDue)
        val interestAmount: TextView = view.findViewById(R.id.interestAmount)
        val profit: TextView = view.findViewById(R.id.profit)
        val docCharges: TextView = view.findViewById(R.id.docCharges)
        val totalProfit: TextView = view.findViewById(R.id.totalProfit)
        val custStatus: TextView = view.findViewById(R.id.custStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        
        holder.customerName.text = "Customer: ${file.customerName}"
        holder.phoneNumber.text = "Phone: ${file.phoneNumber}"
        holder.address.text = "Address: ${file.address}"
        holder.productName.text = "Product: ${file.productName}"
        holder.productModel.text = "Model: ${file.productModel}"
        holder.purchaseDate.text = "Purchase Date: ${file.purchaseDateStr}"
        holder.dueTime.text = "Due Time: ${file.dueTime}"
        holder.dueAmount.text = "Due Amount: ${numberFormat.format(file.dueAmount)}"
        holder.totalDueAmount.text = "Total Due: ${numberFormat.format(file.totalDueAmount)}"
        holder.perMonthDue.text = "Monthly Due: ${numberFormat.format(file.perMonthDue)}"
        holder.interestAmount.text = "Interest: ${numberFormat.format(file.interestAmount)}"
        holder.profit.text = "Profit: ${numberFormat.format(file.profit)}"
        holder.docCharges.text = "Doc Charges: ${numberFormat.format(file.docCharges)}"
        holder.totalProfit.text = "Total Profit: ${numberFormat.format(file.totalProfit)}"
        holder.custStatus.text = "Status: ${file.custStatus}"
    }

    override fun getItemCount() = files.size
} 