package com.asiradnan.muslimapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DynamcAdapter(private var datalist: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER_TYPE = 0
    private val ITEM_TYPE = 1
    private var mlistener: onItemClickListener? = null

    interface onItemClickListener {
        fun buttonClick(position: Int)
        fun holderClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return if (datalist[position] is String) HEADER_TYPE else ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_header, parent, false)
            HeaderViewHolder(itemView)
        }
        else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
            TaskViewHolder(itemView, mlistener)
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(datalist[position] as String)
            is TaskViewHolder -> holder.bind(datalist[position] as Task)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.cat_head)
        val card:CardView = itemView.findViewById(R.id.cat_head_card)

        fun bind(header: String) {
            headerText.text = header.replaceFirstChar {
                it.titlecase()
            }
            val backgroundColor = when (header) {
                "sunnah" -> "#3CB371"
                "nafl" -> "#FFFFE0"
                else -> "#4169E1"
            }
            card.setCardBackgroundColor(Color.parseColor(backgroundColor))
        }
    }

    class TaskViewHolder(itemView: View, listener: onItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val checkbox: Button = itemView.findViewById(R.id.checkBox)

        init {
            checkbox.setOnClickListener {
                listener?.buttonClick(adapterPosition)
            }
            itemView.setOnClickListener {
                listener?.holderClick(adapterPosition)
            }
        }

        fun bind(task: Task) {
            title.text = task.title
        }
    }
}
