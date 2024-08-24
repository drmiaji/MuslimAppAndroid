package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.concurrent.thread

class DailyDutiesFragment : Fragment(R.layout.fragment_daily_duties) {

    private lateinit var recyclerView: RecyclerView
    val taskList = ArrayList<Task>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  now: Calendar = Calendar.getInstance()
        val formatteddate = SimpleDateFormat("dd MMMM").format(now.time)
        val datedisplay:TextView = view.findViewById(R.id.datedisplay)
        datedisplay.text = formatteddate
    }


    override fun onStart() {
        super.onStart()
        Log.d("loggerboi","inside on start")
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        if (access.isNullOrEmpty()) startActivity(Intent(requireContext(),LoginActivity::class.java))
        else{
            recyclerView = view?.findViewById(R.id.recycleview) ?: return
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            Toast.makeText(requireContext(),"Wait..",Toast.LENGTH_SHORT).show()
            thread{
                val url = URL("https://muslimapp.vercel.app/duties/mytask")
                with(url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"
                    setRequestProperty("Authorization","Bearer $access")
                    if (responseCode == 200)
                        inputStream.bufferedReader().use {
                            val json = JSONObject(it.readText())  //important
                            val jsonoarray = json.getJSONArray("tasks")
                            val curr_fard = json.optString("Current_Fard_Percent")
                            val curr_sunnah = json.optString("Current_Sunnah_Percent")
                            val curr_nafl = json.optString("Current_Nafl_Points")
                            activity?.runOnUiThread {
                                updateScore(curr_fard,"fard")
                                updateScore(curr_sunnah,"sunnah")
                                updateScore(curr_nafl,"nafl")
                                showTasks(jsonoarray)
                            }
                        }
                    else
                        activity?.runOnUiThread {
                             Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
    private fun showTasks(jsonarray: JSONArray){
        taskList.clear()
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val task = Task(
                id = taskJson.getInt("id"),
                title = taskJson.getString("title"),
                detail = taskJson.getString("detail"),
                type = taskJson.getString("type")
            )
            taskList.add(task)
        }
        val adapter = Adapter(taskList)
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
        Toast.makeText(requireContext(),"Wait..",Toast.LENGTH_SHORT).show()
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/done/${taskList[position].id}")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization","Bearer $access")
                if (responseCode == 200) {
                    var curr:String
                    inputStream.bufferedReader().use {
                        val response = it.readText()
                        val responseJson = JSONObject(response)
                        if (taskList[position].type == "fard")  curr = responseJson.optString("Current_Fard_Percent")
                        else if (taskList[position].type == "sunnah")  curr = responseJson.optString("Current_Sunnah_Percent")
                        else curr = responseJson.optString("Current_Nafl_Points")
                    }
                    activity?.runOnUiThread {
                        updateScore(curr, taskList[position].type)
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
    private fun updateScore(curr:String, type:String){
        val tochange: CircularProgressIndicator?;
        if (type == "fard")  {
            tochange = view?.findViewById(R.id.percent)
            tochange?.progress = curr.toFloat().toInt()
        }
        else  if (type=="sunnah") {
            tochange = view?.findViewById(R.id.sunnahpercent)
            tochange?.progress = curr.toFloat().toInt()
        }
        else {
            val nafl: TextView? = view?.findViewById(R.id.nafl_points_dd)
            nafl?.text = curr + " Points"
        }
    }
    private fun taskDoneSuccess(position: Int, adapter: Adapter){
        taskList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, taskList.size)
    }
    private fun sendToTaskDetail(position: Int){
        val task = taskList[position]
        val intent = Intent(requireContext(),TaskDetailActivity::class.java)
        intent.putExtra("title",task.title)
        intent.putExtra("detail",task.detail)
        startActivity(intent)
    }
}