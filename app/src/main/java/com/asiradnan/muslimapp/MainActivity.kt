package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedpref = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedpref.getString("accesstoken",null)
        if (access.isNullOrEmpty()) warn();
        else{
            recyclerView = findViewById(R.id.recycleview)
            recyclerView.layoutManager = LinearLayoutManager(this) // or GridLayoutManager

            thread{
                val url: URL = URL("https://muslimapp.vercel.app/duties/mytask")
                with(url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"
                    setRequestProperty("Authorization","Bearer $access")
                    if (responseCode == 200){
                        inputStream.bufferedReader().use {
                            val jsonobject = JSONArray(it.readText());
                            runOnUiThread {
                                Log.d("loggerboi",jsonobject.toString())
                                showTasks(jsonobject);
                            }
                        }
                    }
                    else{
                        runOnUiThread { warn() }
                    }
                }
            }
        }


        val  now:Calendar = Calendar.getInstance();
        val formatteddate = SimpleDateFormat("dd MMMM").format(now.time);
        val datedisplay:TextView = findViewById(R.id.datedisplay)
        datedisplay.text = formatteddate;



        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    private fun showTasks(jsonarray: JSONArray){
        val warning:TextView = findViewById(R.id.warnlogin)
        warning.visibility = View.GONE

        val taskList = ArrayList<Task>()

        for (i in 0 until jsonarray.length()) {
            val taskJson = jsonarray.getJSONObject(i)
            Log.d("loggerboi",taskJson.toString())
            val task = Task(
                title = taskJson.getString("title")
                // add other fields as necessary
            )
            taskList.add(task)
        }
        Log.d("loggerboi",taskList.toString())
        recyclerView.adapter = Adapter(taskList);
    }
    private fun warn(){
        val warning:TextView = findViewById(R.id.warnlogin)
        warning.visibility = View.VISIBLE; 
    }
}