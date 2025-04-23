package com.smbfinance.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smbfinance.R
import com.smbfinance.HistoryActivity
import com.smbfinance.model.DashboardItem

class DashboardAdapter(private val items: List<DashboardItem>) : 
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
        val cardView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconResId)
        holder.title.text = item.title
        holder.description.text = item.description

        holder.cardView.setOnClickListener {
            when (item.title) {
                "History" -> {
                    val intent = Intent(holder.cardView.context, HistoryActivity::class.java)
                    holder.cardView.context.startActivity(intent)
                }
                // Add other cases for different dashboard items
            }
        }
    }

    override fun getItemCount() = items.size
} 