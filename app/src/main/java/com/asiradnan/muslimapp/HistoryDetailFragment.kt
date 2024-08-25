package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HistoryDetailFragment : Fragment(R.layout.fragment_history_detail) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView3: RecyclerView
    val taskList3 = ArrayList<Task>()
    val taskList = ArrayList<Task>()
    val taskList2 = ArrayList<Task>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = arguments?.getString("date")
        val sharedPreferences = requireContext().getSharedPreferences("authorization",Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken",null)
        if (access.isNullOrEmpty()) startActivity(Intent(requireContext(),LoginActivity::class.java))
        else{
            val header:TextView = view.findViewById(R.id.historydetailheader)
            header.text = date
            Toast.makeText(requireContext(),"Loading...",Toast.LENGTH_LONG).show()
            thread{
                val url = URL("https://muslimapp.vercel.app/duties/history_detail_incomplete/$date")
                with (url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $access")
                    if (responseCode == 200) {
                        inputStream.bufferedReader().use {
                            val jsonarray = JSONArray(it.readText()) //important
                            activity?.runOnUiThread {
                                if (date != null) {
                                    showHistoryIncompleteTask(jsonarray)
                                    fetchComplete(access,date)
                                }
                            }
                        }
                    }
                }
            }
            recyclerView2 = view.findViewById(R.id.historyIncompleteRecyclerView)
            recyclerView2.layoutManager = LinearLayoutManager(requireContext())
            recyclerView = view.findViewById(R.id.historyRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView3 = view.findViewById(R.id.historyLateRecyclerView)
            recyclerView3.layoutManager = LinearLayoutManager(requireContext())

        }
    }
    private fun fetchComplete(access:String,date:String){
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/history_detail/$date")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $access")
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        val jsonarray = JSONArray(it.readText()) //important
                        activity?.runOnUiThread {
                            showHistoryTask(jsonarray)
                            fetchLateComplete()
                        }
                    }
                }
            }
        }
    }
    private fun fetchLateComplete(){
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        val date = arguments?.getString("date")
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/history_detail_late/$date")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $access")
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        val jsonarray = JSONArray(it.readText()) //important
                        activity?.runOnUiThread {
                            showLateHistoryTask(jsonarray)
                        }
                    }
                }
            }
        }
    }
    private fun showLateHistoryTask(jsonarray : JSONArray){
        taskList3.clear()
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val id = taskJson.getInt("task__id")
            val task = Task(
                id = id,
                title = taskJson.getString("task__title"),
                detail = taskJson.getString("task__detail"),
                type = taskJson.getString("task__type")
            )
            taskList3.add(task)
        }
        val adapter = HistoryAdapter(taskList3)
        recyclerView3.adapter = adapter
    }
    private fun showHistoryTask(jsonarray: JSONArray) {
        taskList.clear()
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val id = taskJson.getInt("task__id")
            val task = Task(
                id = id,
                title = taskJson.getString("task__title"),
                detail = taskJson.getString("task__detail"),
                type = taskJson.getString("task__type")
            )
            taskList.add(task)
        }
        val adapter = HistoryAdapter(taskList)
        recyclerView.adapter = adapter
    }
    private fun showHistoryIncompleteTask(jsonarray: JSONArray) {
        taskList2.clear()
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val task = Task(
                id = taskJson.getInt("id"),
                title = taskJson.getString("title"),
                detail = taskJson.getString("detail"),
                type = taskJson.getString("type")
            )
            taskList2.add(task)
        }
        val adapter = Adapter(taskList2)
        recyclerView2.adapter = adapter
        adapter.onItemClickListener(object : Adapter.onItemClickListener{
            override fun buttonClick(position: Int) {
                taskDone(position, adapter)
            }
            override fun holderClick(position: Int) {
                sendToTaskDetail(position)
            }
        })
    }
    private fun taskDone(position:Int, adapter: Adapter){
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        val date = arguments?.getString("date")
        Toast.makeText(requireContext(),"Wait..",Toast.LENGTH_SHORT).show()
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/done_old/${taskList2[position].id}/$date")
            Log.d("loggerboi", url.toString())
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization","Bearer $access")
                if (responseCode == 200) {
                    activity?.runOnUiThread {
                        taskDoneSuccess(position,adapter)
                    }
                }
                else
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun sendToTaskDetail(position: Int){
        val task = taskList[position]
        val intent = Intent(requireContext(),TaskDetailActivity::class.java)
        intent.putExtra("title",task.title)
        intent.putExtra("detail",task.detail)
        startActivity(intent)
    }
    private fun taskDoneSuccess(position: Int, adapter: Adapter){
        taskList2.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, taskList2.size)
        fetchLateComplete()
    }
}