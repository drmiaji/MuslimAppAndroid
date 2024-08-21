package com.asiradnan.muslimapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter(private var datelist:ArrayList<Date>):RecyclerView.Adapter<DateAdapter.ViewHolder> (){

    private lateinit var mlistener: onItemClickListener
    interface onItemClickListener{
        fun holderClick(position: Int)
    }

    fun onItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.date_layout,parent,false)
        return ViewHolder(itemView,mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curr = datelist[position]
        val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        holder.date.text = formatter.format(curr)
    }

    override fun getItemCount(): Int {
        return datelist.size
    }

    class ViewHolder (itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView) {
        val date:TextView = itemView.findViewById(R.id.history_date)

        init{
            itemView.setOnClickListener{
                listener.holderClick(adapterPosition)
            }
        }
    }

}