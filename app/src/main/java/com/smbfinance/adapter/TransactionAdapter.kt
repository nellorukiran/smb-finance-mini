package com.smbfinance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smbfinance.R
import com.smbfinance.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTransactionId: TextView = view.findViewById(R.id.tvTransactionId)
        val tvPaidDate: TextView = view.findViewById(R.id.tvPaidDate)
        val tvPaidAmount: TextView = view.findViewById(R.id.tvPaidAmount)
        val tvBalanceDue: TextView = view.findViewById(R.id.tvBalanceDue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvTransactionId.text = transaction.transactionId
        holder.tvPaidDate.text = formatDate(transaction.paidDate)
        holder.tvPaidAmount.text = "₹${transaction.paidDue}"
        holder.tvBalanceDue.text = "₹${transaction.balanceDue}"
    }

    override fun getItemCount() = transactions.size

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }
} 