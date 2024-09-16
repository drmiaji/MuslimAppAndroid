package com.asiradnan.muslimapp.fragments

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asiradnan.muslimapp.dataclasses.HistoryPoints
import com.asiradnan.muslimapp.R
import com.asiradnan.muslimapp.activities.MainActivity
import com.asiradnan.muslimapp.adapters.DateAdapter
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar


class HistoryFragment : Fragment(R.layout.fragment_history) {
    private lateinit var recyclerView: RecyclerView
    val historypointslist = ArrayList<HistoryPoints>()
    private lateinit var jsonarray: JSONArray

    override fun onStart() {
        super.onStart()
        recyclerView = view?.findViewById(R.id.dateRecyclerView) ?: return
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.history.observe(viewLifecycleOwner) { json ->
            jsonarray = json
            showDates()
        }
    }
    private fun showDates() {
        historypointslist.clear()
        val  now: Calendar = Calendar.getInstance()
        val formatteddate = SimpleDateFormat("yyyy-MM-dd").format(now.time)
        Log.d("loggerboi",formatteddate)
        for (i in 0 until jsonarray.length()){
            val json = jsonarray.getJSONObject(i)
            val historypoint = HistoryPoints(
                date = json.getString("date"),
                fard = json.getDouble("fard_percent"),
                sunnah = json.getDouble("sunnah_percent"),
                nafl = json.getInt("nafl_points")
            )
            if (historypoint.date != formatteddate) historypointslist.add(historypoint)
        }
        if (historypointslist.size != 0) {
            val nohistorytext:TextView? = view?.findViewById(R.id.nohistorytext)
            nohistorytext?.visibility = View.GONE
        }
        val adapter = DateAdapter(historypointslist)
        recyclerView.adapter = adapter
        adapter.onItemClickListener(object : DateAdapter.onItemClickListener {
            override fun holderClick(position: Int) {
                sendToHistoryDetail(position)
            }
        })
    }

    private fun sendToHistoryDetail(position: Int) {
        val bundle = Bundle()
        bundle.putString("date", historypointslist[position].date)
        (activity as? MainActivity)?.navigateToHistoryDetail(bundle)
    }
}