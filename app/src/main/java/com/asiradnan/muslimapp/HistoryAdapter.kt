package com.asiradnan.muslimapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter (private var datalist:ArrayList<HistoryTask>):RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.historytask_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = datalist[position]
        holder.title.text = currentItem.title

    }
    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
        val title:TextView = itemView.findViewById(R.id.historytask)
    }
}