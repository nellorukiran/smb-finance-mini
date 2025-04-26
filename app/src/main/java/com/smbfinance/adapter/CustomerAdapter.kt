package com.smbfinance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smbfinance.R
import com.smbfinance.model.Customer

class CustomerAdapter(private val onItemClick: (Customer) -> Unit) :
    ListAdapter<Customer, CustomerAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CustomerViewHolder(
        itemView: View,
        private val onItemClick: (Customer) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(customer: Customer) {
            tvName.text = customer.name
            tvPhone.text = customer.phone
            tvAmount.text = "₹${customer.amount}"

            itemView.setOnClickListener { onItemClick(customer) }
        }
    }

    private class CustomerDiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }
} 