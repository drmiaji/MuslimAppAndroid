package com.asiradnan.muslimapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter (private var datalist:ArrayList<Task>):RecyclerView.Adapter<Adapter.ViewHolder>(){

    private lateinit var mlistener: onItemClickListener
    interface onItemClickListener{
        fun buttonClick(position: Int)
        fun holderClick(position: Int)
    }

    fun onItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView,mlistener)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("loggerboi","onbindview")
        val currentItem = datalist[position]
        holder.title.text = currentItem.title
        if (currentItem.type == "sunnah") holder.title.setBackgroundColor(Color.parseColor("#3CB371"));
        else if (currentItem.type == "nafl") holder.title.setBackgroundColor(Color.parseColor("#B8860B"));

    }
    class ViewHolder (itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView) {
        val title:TextView = itemView.findViewById(R.id.title)
        val checkbox:Button = itemView.findViewById(R.id.checkBox)

        init{
            checkbox.setOnClickListener{
                listener.buttonClick(adapterPosition)
            }
            itemView.setOnClickListener{
                listener.holderClick(adapterPosition)
            }
        }
    }

}