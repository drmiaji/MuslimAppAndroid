package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.concurrent.thread


class HistoryFragment : Fragment(R.layout.fragment_history) {
    private lateinit var recyclerView: RecyclerView
    val datelist = ArrayList<Date>()
    private lateinit var jsonobject: JSONObject
    private lateinit var dateStrings:MutableList<String>

    override fun onStart() {
        super.onStart()
        val sharedPreference = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedPreference.getString("accesstoken", null)
        if (access.isNullOrEmpty()) warn()
        else {
            recyclerView = view?.findViewById(R.id.dateRecyclerView) ?: return
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            Toast.makeText(requireContext(), "Wait..", Toast.LENGTH_SHORT).show()
            thread {
                val url = URL("https://muslimapp.vercel.app/duties/myhistory")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $access")
                    if (responseCode == 200) {
                        inputStream.bufferedReader().use {
                            jsonobject = JSONObject(it.readText()) //important
                            activity?. runOnUiThread {
                                showDates()
                            }
                        }
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "Could not load", Toast.LENGTH_SHORT).show()
                            warn()
                        }
                    }
                }
            }
        }
    }
    private fun showDates(){
        val warning:TextView? = view?.findViewById(R.id.warnloginhistory)
        warning?.visibility = View.GONE

        datelist.clear()
        val keys = jsonobject.keys()
        dateStrings = mutableListOf()
        while (keys.hasNext()) {
            val key = keys.next()
            dateStrings.add(key)
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date: Date = formatter.parse(key)
            datelist.add(date)
        }
        val adapter = DateAdapter(datelist)
        recyclerView.adapter = adapter
        adapter.onItemClickListener(object : DateAdapter.onItemClickListener{
            override fun holderClick(position: Int) {
                sendToHistoryDetail(position)
            }
        })
    }
    private fun sendToHistoryDetail(position: Int){
        val intent = Intent(requireContext(), HistoryDetailActivity::class.java)
        val arr = jsonobject.getJSONArray(dateStrings[position])
        intent.putExtra("list",arr.toString())
        startActivity(intent)
    }

    private fun warn() {
        val warning: TextView? = view?.findViewById(R.id.warnloginhistory)
        warning?.visibility = View.VISIBLE
    }
}