package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
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
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.concurrent.thread

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val datelist = ArrayList<Date>()
    private lateinit var jsonobject:JSONObject
    private lateinit var dateStrings:MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.menu_item_history
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_item_history ->true
                R.id.menu_item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }
    override fun onStart() {
        super.onStart()
        val sharedPreference = getSharedPreferences("authorization",Context.MODE_PRIVATE)
        val access = sharedPreference.getString("accesstoken",null)
        if (access.isNullOrEmpty()) warn()
        else{
            recyclerView = findViewById(R.id.dateRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            Toast.makeText(this,"Wait..",Toast.LENGTH_SHORT).show()
            thread{
                val url = URL("https://muslimapp.vercel.app/duties/myhistory")
                with (url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"
                    setRequestProperty("Authorization","Bearer $access")
                    if (responseCode == 200){
                        inputStream.bufferedReader().use {
                            jsonobject = JSONObject(it.readText()) //important
                            runOnUiThread { showDates() }
                        }
                    }
                    else {
                        runOnUiThread {
                            Toast.makeText(this@HistoryActivity,"Could not load",Toast.LENGTH_SHORT).show()
                            warn()
                        }
                    }
                }
            }
        }

    }
    private fun showDates(){
        val warning:TextView = findViewById(R.id.warnloginhistory)
        warning.visibility = View.GONE

        datelist.clear()
        val keys = jsonobject.keys()
        dateStrings = mutableListOf()
        while (keys.hasNext()) {
            val key = keys.next()
            dateStrings.add(key)
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date: Date = formatter.parse(key)
            datelist.add(date)
        }
        val adapter = DateAdapter(datelist)
        recyclerView.adapter = adapter
        adapter.onItemClickListener(object : DateAdapter.onItemClickListener{
            override fun holderClick(position: Int) {
                sendToHistoryDetail(position)
            }
        })
    }
    private fun sendToHistoryDetail(position: Int){
        val intent = Intent(this, HistoryDetailActivity::class.java)
        val arr = jsonobject.getJSONArray(dateStrings[position])
        intent.putExtra("list",arr.toString())
        startActivity(intent)
    }
    private fun warn() {
        val warning: TextView = findViewById(R.id.warnloginhistory)
        warning.visibility = View.VISIBLE
    }
}