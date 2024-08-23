package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var recyclerView2: RecyclerView
    val taskList = ArrayList<Task>()
    val taskList2 = ArrayList<Task>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = arguments?.getString("date")
        if (date != null) {
            Log.d("loggrboi",date)
        }
        val sharedPreferences = requireContext().getSharedPreferences("authorization",Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken",null)
        if (access.isNullOrEmpty()) startActivity(Intent(requireContext(),LoginActivity::class.java))
        else{
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
                        }
                    }
                }
            }
        }
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
        Log.d("loggerboi",taskList.toString())
        val adapter = HistoryAdapter(taskList)
        recyclerView.adapter = adapter
    }
    private fun showHistoryIncompleteTask(jsonarray: JSONArray) {
        Log.d("loggerboi","inside func")
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
        Log.d("loggerboi",taskList2.toString())
        val adapter = Adapter(taskList2)
Log.d("loggerboi","before recycle")
        recyclerView2.adapter = adapter
        recyclerView.adapter = adapter
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
//        thread {
//            val url = URL("https://muslimapp.vercel.app/duties/done/${taskList[position].id}")
//            with(url.openConnection() as HttpURLConnection) {
//                requestMethod = "GET"
//                setRequestProperty("Authorization","Bearer $access")
//                if (responseCode == 200) {
//                    var curr:String
//                    inputStream.bufferedReader().use {
//                        val response = it.readText()
//                        val responseJson = JSONObject(response)
//                        if (taskList[position].type == "fard")  curr = responseJson.optString("Current_Fard_Percent")
//                        else if (taskList[position].type == "sunnah")  curr = responseJson.optString("Current_Sunnah_Percent")
//                        else curr = responseJson.optString("Current_Nafl_Points")
//                    }
//                    activity?.runOnUiThread {
//                        updateScore(curr, taskList[position].type)
//                        taskDoneSuccess(position,adapter)
//                    }
//                }
//                else
//                    activity?.runOnUiThread {
//                    Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }
    private fun sendToTaskDetail(position: Int){
        val task = taskList[position]
        val bundle = Bundle()
        bundle.putString("title",task.title)
        bundle.putString("detail",task.detail)
        (activity as? MainActivity)?.navigateToTaskDetail(bundle)
    }
}