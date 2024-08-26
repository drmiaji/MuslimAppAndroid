package com.asiradnan.muslimapp

import SharedViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    val anyList = ArrayList<Any>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  now: Calendar = Calendar.getInstance()
        val formatteddate = SimpleDateFormat("dd MMMM").format(now.time)
        val datedisplay:TextView = view.findViewById(R.id.datedisplay)
        datedisplay.text = formatteddate


        val calendar: Button = view.findViewById(R.id.calendarbtn)
        val qibla: Button = view.findViewById(R.id.button5)
        qibla.setOnClickListener {
            startActivity(Intent(requireContext(),QiblaActivity::class.java))
        }
        val prayertimebtn : Button = view.findViewById(R.id.prayertimebutton)
        prayertimebtn.setOnClickListener {
            val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
            model.prayertimes.observe(viewLifecycleOwner) { jsonObject ->
                val intent = Intent(requireContext(),PrayerTimeActivity::class.java)
                intent.putExtra("Fajr", jsonObject.optString("Fajr"))
                intent.putExtra("Sunrise", jsonObject.optString("Sunrise"))
                intent.putExtra("Dhuhr", jsonObject.optString("Dhuhr"))
                intent.putExtra("Asr", jsonObject.optString("Asr"))
                intent.putExtra("Maghrib", jsonObject.optString("Maghrib"))
                intent.putExtra("Sunset", jsonObject.optString("Sunset"))
                intent.putExtra("Isha", jsonObject.optString("Isha"))
                startActivity(intent)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        if (access.isNullOrEmpty()) startActivity(Intent(requireContext(),LoginActivity::class.java))
        else{
            recyclerView = view?.findViewById(R.id.recycleview) ?: return
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        anyList.clear()
        var last:String = "last";
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            val task = Task(
                id = taskJson.getInt("id"),
                title = taskJson.getString("title"),
                detail = taskJson.getString("detail"),
                type = taskJson.getString("type")
            )
            if (last != task.type) {
                last = task.type
                anyList.add(last)
            }
            anyList.add(task)
        }
        val adapter = DynamcAdapter(anyList)
        recyclerView.adapter = adapter
         adapter.setOnItemClickListener(object : DynamcAdapter.onItemClickListener {
            override fun buttonClick(position: Int) {
                taskDone(position, adapter)
            }
            override fun holderClick(position: Int) {
                sendToTaskDetail(position)
            }
        })
    }
    private fun taskDone(position:Int, adapter: DynamcAdapter){
        val task:Task = anyList[position] as Task
        val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        Toast.makeText(requireContext(),"Wait..",Toast.LENGTH_SHORT).show()
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/done/${task.id}")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization","Bearer $access")
                if (responseCode == 200) {
                    var curr:String
                    inputStream.bufferedReader().use {
                        val response = it.readText()
                        val responseJson = JSONObject(response)
                        if (task.type == "fard")  curr = responseJson.optString("Current_Fard_Percent")
                        else if (task.type == "sunnah")  curr = responseJson.optString("Current_Sunnah_Percent")
                        else curr = responseJson.optString("Current_Nafl_Points")
                    }
                    activity?.runOnUiThread {
                        updateScore(curr, task.type)
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
    private fun taskDoneSuccess(position: Int, adapter: DynamcAdapter){
        anyList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, anyList.size)
    }
    private fun sendToTaskDetail(position: Int){
        val task = anyList[position] as Task
        val intent = Intent(requireContext(),TaskDetailActivity::class.java)
        intent.putExtra("title",task.title)
        intent.putExtra("detail",task.detail)
        startActivity(intent)
    }
}