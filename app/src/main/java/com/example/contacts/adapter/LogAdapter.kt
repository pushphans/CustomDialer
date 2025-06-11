package com.example.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.contactlogs.Logs

class LogAdapter(
    private val onClick : (Logs) -> Unit,
    private val longClick : (Logs) -> Unit)
    : ListAdapter<Logs, LogAdapter.LogViewHolder>(DiffCallback()){

    inner class LogViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.tvName)
        val number = view.findViewById<TextView>(R.id.tvNumber)
        val iconType = view.findViewById<ImageView>(R.id.icon)
    }

    class DiffCallback() : DiffUtil.ItemCallback<Logs>(){
        override fun areItemsTheSame(oldItem: Logs, newItem: Logs): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Logs, newItem: Logs): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.logs_item_layout, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val item = getItem(position)
        holder.name.text = item.name
        holder.number.text = item.number
        holder.iconType.setImageResource(item.iconRes)

        holder.itemView.setOnClickListener {
            onClick(item)
        }

        holder.itemView.setOnLongClickListener {
            longClick(item)
            true
        }

    }
}