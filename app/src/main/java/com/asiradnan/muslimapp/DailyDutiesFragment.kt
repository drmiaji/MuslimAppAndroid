package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
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
                            val jsonobject = JSONArray(it.readText()) //important
                            activity?.runOnUiThread {
                                showTasks(jsonobject) }
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
                detail = taskJson.getString("detail")
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
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/done/${taskList[position].id}")
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
    private fun taskDoneSuccess(position: Int, adapter: Adapter){
        taskList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, taskList.size)
    }
    private fun sendToTaskDetail(position: Int){
        val task = taskList[position]
        val bundle = Bundle()
        bundle.putString("title",task.title)
        bundle.putString("detail",task.detail)
        val fragment = TaskDetailFragment()
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().apply(){
            replace(R.id.mainframelayout,fragment)
            commit()
        }
    }
}