package com.asiradnan.muslimapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HistoryDetailFragment : Fragment(R.layout.fragment_history_detail) {
    private lateinit var recyclerView: RecyclerView
    val taskList = ArrayList<HistoryTask>()
    var mp = mutableMapOf<Int,String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arr = arguments?.getString("list")
        thread{
            val url = URL("https://muslimapp.vercel.app/duties/tasks")
            with (url.openConnection() as HttpURLConnection){
                requestMethod = "GET"
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        val jsonarray = JSONArray(it.readText()) //important
                        activity?.runOnUiThread {
                            if (arr != null) {
                                makeMap(jsonarray)
                                showHistoryTask(arr)
                            }
                        }
                    }
                }
            }
        }
        recyclerView = view?.findViewById(R.id.historyRecyclerView) ?: return
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
     private fun makeMap(jsonarray: JSONArray) {
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            mp[taskJson.getInt("id")] = taskJson.getString("title")
        }
    }
    private fun showHistoryTask(arr: String) {
        val jsonarray = JSONArray(arr)
        taskList.clear()
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val id = taskJson.getInt("task")
            val task = mp[id]?.let {
                HistoryTask(
                    id = id,
                    title = it,
                    frequency = taskJson.getInt("frequency")
                )
            }
            if (task != null) {
                taskList.add(task)
            }
        }
        val adapter = HistoryAdapter(taskList)
        recyclerView.adapter = adapter
    }
}