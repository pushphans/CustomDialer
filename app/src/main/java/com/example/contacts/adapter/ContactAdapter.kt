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
import com.example.contacts.room.Contacts

class ContactAdapter(
    private val onClick : (Contacts) -> Unit,
    private val longClick : (Contacts) -> Unit)
    : ListAdapter<Contacts, ContactAdapter.ContactsViewHolder>(DiffCallback()) {

    inner class ContactsViewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.tvName)
        val number = view.findViewById<TextView>(R.id.tvNumber)
        val btnCall = view.findViewById<ImageView>(R.id.btnCall)
    }

    class DiffCallback() : DiffUtil.ItemCallback<Contacts>(){
        override fun areItemsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
            return oldItem.id == newItem.id || oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_item_layout, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val item = getItem(position)
        holder.name.text = item.name
        holder.number.text = item.number

        holder.btnCall.setOnClickListener {
            onClick(item)
        }

        holder.itemView.setOnLongClickListener {
            longClick(item)
            true
        }
    }
}