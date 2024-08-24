package com.asiradnan.muslimapp

import SharedViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.concurrent.thread


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
        for (i in 0 until jsonarray.length()){
            val json = jsonarray.getJSONObject(i)
            val historypoint = HistoryPoints(
                date = json.getString("date"),
                fard = json.getDouble("fard_percent"),
                sunnah = json.getDouble("sunnah_percent"),
                nafl = json.getInt("nafl_points")
            )
            historypointslist.add(historypoint)
        }
        historypointslist.reverse()
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