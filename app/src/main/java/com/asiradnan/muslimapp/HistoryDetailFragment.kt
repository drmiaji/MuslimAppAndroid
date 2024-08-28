package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    val taskList3 = ArrayList<Any>()
    val taskList = ArrayList<Task>()
    val taskList2 = ArrayList<Any>()
    val lateadapter = DynamicAdapter2(taskList3)
    val incompleteadapter = DynamcAdapter(taskList2)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = arguments?.getString("date")
        val sharedPreferences = requireContext().getSharedPreferences("authorization",Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken",null)
        if (access.isNullOrEmpty()) startActivity(Intent(requireContext(),LoginActivity::class.java))
        else if (date.isNullOrEmpty()) Toast.makeText(requireContext(),"No Date Found",Toast.LENGTH_LONG).show()
        else{
            val swicth1:Button = view.findViewById(R.id.ontimehistorytoggle)
            val swicth2: Button = view.findViewById(R.id.latecompletedhistorytoggle)
            val swicth3: Button = view.findViewById(R.id.incompletehistorytoggle)
            val header1:TextView = view.findViewById(R.id.completedLateTaskheader)
            val header2:TextView = view.findViewById(R.id.completedTaskheader)
            val header3:TextView = view.findViewById(R.id.incompletedTaskheader)
            recyclerView2 = view.findViewById(R.id.historyIncompleteRecyclerView)
            recyclerView2.layoutManager = LinearLayoutManager(requireContext())
            recyclerView = view.findViewById(R.id.historyRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView3 = view.findViewById(R.id.historyLateRecyclerView)
            recyclerView3.layoutManager = LinearLayoutManager(requireContext())
            header1.visibility = View.GONE
            recyclerView.visibility = View.GONE
            header2.visibility = View.GONE
            recyclerView2.visibility = View.VISIBLE
            header3.visibility = View.VISIBLE
            recyclerView3.visibility = View.GONE

            swicth3.setOnClickListener {
                header1.visibility = View.GONE
                recyclerView.visibility = View.GONE
                header2.visibility = View.GONE
                header3.visibility = View.VISIBLE
                recyclerView2.visibility = View.VISIBLE
                recyclerView3.visibility = View.GONE
            }
            swicth2.setOnClickListener {
                header1.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                header2.visibility = View.GONE
                recyclerView2.visibility = View.GONE
                header3.visibility = View.GONE
                recyclerView3.visibility = View.VISIBLE
            }
            swicth1.setOnClickListener {
                header2.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                header1.visibility = View.GONE
                recyclerView2.visibility = View.GONE
                header3.visibility = View.GONE
                recyclerView3.visibility = View.GONE
            }
            val header:TextView = view.findViewById(R.id.historydetailheader)
            header.text = date
            fetchIncomplete(access,date)



        }
    }
    private fun fetchIncomplete(access: String, date: String) {
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/history_detail_incomplete/$date")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $access")
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        val jsonarray = JSONArray(it.readText()) //important
                        activity?.runOnUiThread {
                            if (date != null) {
                                showHistoryIncompleteTask(jsonarray)
                                fetchComplete(access, date)
                            }
                        }
                    }
                }
            }
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
        var last:String = "last";
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val id = taskJson.getInt("task__id")
            val task = Task(
                id = id,
                priority = taskJson.getInt("task__priority"),
                title = taskJson.getString("task__title"),
                detail = taskJson.getString("task__detail"),
                type = taskJson.getString("task__type")
            )
            if (last != task.type) {
                last = task.type
                taskList3.add(last)
            }
            taskList3.add(task)
        }
        recyclerView3.adapter = lateadapter
         lateadapter.setOnItemClickListener(object : DynamicAdapter2.onItemClickListener {
            override fun buttonClick(position: Int) {
                taskUndo(position)
            }
            override fun holderClick(position: Int) {
                sendToTaskDetail(position)
            }
        })
    }
    private fun showHistoryTask(jsonarray: JSONArray) {
        taskList.clear()
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val id = taskJson.getInt("task__id")
            val task = Task(
                id = id,
                priority = taskJson.getInt("task__priority"),
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
        var last:String = "last";
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val task = Task(
                id = taskJson.getInt("id"),
                priority = taskJson.getInt("priority"),
                title = taskJson.getString("title"),
                detail = taskJson.getString("detail"),
                type = taskJson.getString("type")
            )
            if (last != task.type) {
                last = task.type
                taskList2.add(last)
            }
            taskList2.add(task)
        }

        recyclerView2.adapter = incompleteadapter
        incompleteadapter.setOnItemClickListener(object : DynamcAdapter.onItemClickListener{
            override fun buttonClick(position: Int) {
                taskDone(position, incompleteadapter)
            }
            override fun holderClick(position: Int) {
                sendToTaskDetail(position)
            }
        })
    }
    private fun taskDone(position:Int, adapter: DynamcAdapter){
        val task:Task = taskList2[position] as Task
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        val date = arguments?.getString("date")
        if (isInternetAvailable(requireContext())) {
            val temp = ArrayList<Task>()
            for (i in taskList3) {
                if (i is Task) temp.add(i)
            }
            temp.add(task)
            temp.sortBy { it.priority }
            taskList3.clear()
            var last = "last";
            for (i in temp) {
                if (last != i.type) {
                    last = i.type
                    taskList3.add(last)
                }
                taskList3.add(i)
            }
            lateadapter.notifyDataSetChanged()
            taskList2.removeAt(position)
            incompleteadapter.notifyItemRemoved(position)
            incompleteadapter.notifyItemRangeChanged(position, taskList2.size)
            thread {
            val url = URL("https://muslimapp.vercel.app/duties/done_old/${task.id}/$date")
            Log.d("loggerboi", url.toString())
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization","Bearer $access")
                if (responseCode == 200) {
                    activity?.runOnUiThread {
                        if (access != null) {
                            if (date != null) {
                                fetchIncomplete(access,date)
                            }
                        }
                    }
                }
                else
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                    }
            }
        }
        }
        else Toast.makeText(requireContext(),"No Internet",Toast.LENGTH_SHORT).show()

    }
    private fun taskUndo(position:Int){
        val task:Task = taskList3[position] as Task
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        val date = arguments?.getString("date")
        if (isInternetAvailable(requireContext())) {
            val temp = ArrayList<Task>()
            for (i in taskList2) {
                if (i is Task) temp.add(i)
            }
            temp.add(task)
            temp.sortBy { it.priority }
            taskList2.clear()
            var last = "last";
            for (i in temp) {
                if (last != i.type) {
                    last = i.type
                    taskList2.add(last)
                }
                taskList2.add(i)
            }
            incompleteadapter.notifyDataSetChanged()
            taskList3.removeAt(position)
            lateadapter.notifyItemRemoved(position)
            lateadapter.notifyItemRangeChanged(position, taskList2.size)
            thread {
            val url = URL("https://muslimapp.vercel.app/duties/undo_old/${task.id}/$date")
            Log.d("loggerboi", url.toString())
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization","Bearer $access")
                if (responseCode == 200) {
                    activity?.runOnUiThread {
                        if (access != null) {
                            if (date != null) {
                                fetchIncomplete(access,date)
                            }
                        }
                    }
                }
                else
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                    }
            }
        }
        }
        else Toast.makeText(requireContext(),"No Internet",Toast.LENGTH_SHORT).show()

    }
    private fun sendToTaskDetail(position: Int){
        val task = taskList[position]
        val intent = Intent(requireContext(),TaskDetailActivity::class.java)
        intent.putExtra("title",task.title)
        intent.putExtra("detail",task.detail)
        startActivity(intent)
    }
        fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}