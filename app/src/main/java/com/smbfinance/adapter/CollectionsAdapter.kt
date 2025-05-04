package com.smbfinance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smbfinance.R
import com.smbfinance.model.Collection
import java.text.NumberFormat
import java.util.Locale

class CollectionsAdapter(private val collections: List<Collection>) : RecyclerView.Adapter<CollectionsAdapter.ViewHolder>() {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val customerName: TextView = view.findViewById(R.id.customerName)
        val amount: TextView = view.findViewById(R.id.amount)
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber)
        val collectionDate: TextView = view.findViewById(R.id.collectionDate)
        val totalDues: TextView = view.findViewById(R.id.totalDues)
        val balanceDue: TextView = view.findViewById(R.id.balanceDue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = collections[position]
        holder.customerName.text = collection.customerName
        holder.amount.text = numberFormat.format(collection.amount)
        holder.phoneNumber.text = "Phone: ${collection.phoneNumber}"
        holder.collectionDate.text = "Date: ${collection.collectionDate}"
        holder.totalDues.text = "Total Dues: ${collection.totalDues}"
        holder.balanceDue.text = "Balance: ${numberFormat.format(collection.balanceDue)}"
    }

    override fun getItemCount() = collections.size
} 