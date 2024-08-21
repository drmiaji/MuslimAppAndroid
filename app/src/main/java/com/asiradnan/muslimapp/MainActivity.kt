package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val taskList = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val  now:Calendar = Calendar.getInstance()
        val formatteddate = SimpleDateFormat("dd MMMM").format(now.time)
        val datedisplay:TextView = findViewById(R.id.datedisplay)
        datedisplay.text = formatteddate

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> true
                R.id.menu_item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.menu_item_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val sharedpref = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        if (access.isNullOrEmpty()) warn()
        else{
            recyclerView = findViewById(R.id.recycleview)
            recyclerView.layoutManager = LinearLayoutManager(this)
            Toast.makeText(this,"Wait..",Toast.LENGTH_SHORT).show()
            thread{
                val url = URL("https://muslimapp.vercel.app/duties/mytask")
                with(url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"
                    setRequestProperty("Authorization","Bearer $access")
                    if (responseCode == 200)
                        inputStream.bufferedReader().use {
                            val jsonobject = JSONArray(it.readText()) //important
                            runOnUiThread { showTasks(jsonobject) }
                        }
                    else runOnUiThread { warn() }
                }
            }
        }
    }

    private fun showTasks(jsonarray: JSONArray){
        val warning:TextView = findViewById(R.id.warnlogin)
        warning.visibility = View.GONE
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
        val sharedpref = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        thread {
            val url = URL("https://muslimapp.vercel.app/duties/done/${taskList[position].id}")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization","Bearer $access")
                if (responseCode == 200) {
                    runOnUiThread {
                        taskDoneSuccess(position,adapter)
                    }
                }
                else runOnUiThread {
                    Toast.makeText(this@MainActivity,"Failed",Toast.LENGTH_SHORT).show()
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
        val intent = Intent(this,TaskDetailActivity::class.java)
        intent.putExtra("title",task.title)
        intent.putExtra("detail",task.detail)
        startActivity(intent)
    }
    private fun warn() {
        val warning:TextView = findViewById(R.id.warnlogin)
        warning.visibility = View.VISIBLE
    }
}