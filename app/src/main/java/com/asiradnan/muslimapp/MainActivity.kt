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
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
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
        val access = sharedpref.getString("access",null)
        if (access.isNullOrEmpty()) warn();
        else{
            thread{
                val url: URL = URL("https://muslimapp.vercel.app/duties/mytask")
                with(url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"
                    setRequestProperty("Authorization","Bearer $access")
                    if (responseCode == 200){
                        inputStream.bufferedReader().use {
                            val jsonobject: JSONObject = JSONObject(it.readText());
                            runOnUiThread {
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
    private fun warn(){
        val warning:TextView = findViewById(R.id.warnlogin)
        warning.visibility = View.VISIBLE; 
    }
}