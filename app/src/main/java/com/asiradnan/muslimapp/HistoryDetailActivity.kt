package com.asiradnan.muslimapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val taskList = ArrayList<HistoryTask>()
    var mp = mutableMapOf<Int,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val arr = intent.getStringExtra("list")
        thread{
            val url = URL("https://muslimapp.vercel.app/duties/tasks")
            with (url.openConnection() as HttpURLConnection){
                requestMethod = "GET"
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        val jsonarray = JSONArray(it.readText()) //important
                        runOnUiThread {
                            if (arr != null) {
                                makeMap(jsonarray)
                                Log.d("loggerboi", "ready for cycle")
                                showHistoryTask(arr)
                            }
                        }
                    }
                }
            }
        }
        recyclerView = findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.menu_item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.menu_item_history -> true
                else -> false
            }
        }
    }
    private fun makeMap(jsonarray: JSONArray) {
        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            mp[taskJson.getInt("id")] = taskJson.getString("title")
        }
    }
    private fun showHistoryTask(arr: String) {
        val jsonarray = JSONArray(arr)
        Log.d("loggerboi", "inside show func")
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
        Log.d("loggerboi", taskList.toString())
        val adapter = HistoryAdapter(taskList)
        Log.d("loggerboi", "Adapter ready, just recycling now")
        recyclerView.adapter = adapter
    }
}