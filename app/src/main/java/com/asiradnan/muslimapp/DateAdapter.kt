package com.asiradnan.muslimapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator

class DateAdapter(private var historypointslist:ArrayList<HistoryPoints>):RecyclerView.Adapter<DateAdapter.ViewHolder> (){

    private lateinit var mlistener: onItemClickListener
    interface onItemClickListener{
        fun holderClick(position: Int)
    }

    fun onItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.historypoints_layout,parent,false)
        return ViewHolder(itemView,mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curr = historypointslist[position]
        holder.date.text = curr.date
        holder.fard.progress = curr.fard.toInt()
        holder.sunnah.progress = curr.sunnah.toInt()
        holder.nafl.text = curr.nafl.toString() + " Points  "
    }

    override fun getItemCount(): Int {
        return historypointslist.size
    }

    class ViewHolder (itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView) {
        val date:TextView = itemView.findViewById(R.id.history_date)
        val fard:CircularProgressIndicator = itemView.findViewById(R.id.history_fard)
        val sunnah:CircularProgressIndicator = itemView.findViewById(R.id.history_sunnah)
        val nafl:TextView = itemView.findViewById(R.id.history_nafl)
        init{
            itemView.setOnClickListener{
                listener.holderClick(adapterPosition)
            }
        }
    }

}