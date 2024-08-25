package com.asiradnan.muslimapp

import SharedViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private var isNavigatingFromBottomNav = false

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
        val loggedin = sharedpref.getString("loggedin", null)
        if (!loggedin.isNullOrEmpty()) {
            fetchProfileData()
            fetchHistoryDates()
            fetchPrayertimes()
            val viewPager: ViewPager2 = findViewById(R.id.viewPager)
            val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                        viewPager.visibility = View.VISIBLE
                        bottomNavigationView.visibility = View.VISIBLE
                    } else {
                        if (viewPager.currentItem == 0) finish()
                        bottomNavigationView.selectedItemId = R.id.menu_item_home
                    }
                }
            })

            val adapter = ViewPagerAdapter(this)
            viewPager.adapter = adapter
            bottomNavigationView.setOnItemSelectedListener { menuItem ->
                isNavigatingFromBottomNav = true
                when (menuItem.itemId) {
                    R.id.menu_item_home -> viewPager.setCurrentItem(0, false)
                    R.id.menu_item_history -> viewPager.setCurrentItem(1, false)
                    R.id.menu_item_feedback -> viewPager.setCurrentItem(2, false)
                    R.id.menu_item_profile -> viewPager.setCurrentItem(3, false)
                }
                true
            }
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (!isNavigatingFromBottomNav) {
                        when (position) {
                            0 -> bottomNavigationView.selectedItemId = R.id.menu_item_home
                            1 -> bottomNavigationView.selectedItemId = R.id.menu_item_history
                            2 -> bottomNavigationView.selectedItemId = R.id.menu_item_feedback
                            3 -> bottomNavigationView.selectedItemId = R.id.menu_item_profile
                        }
                    }
                    isNavigatingFromBottomNav = false
                }
            })
        }
        else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    override fun onRestart() {
        super.onRestart()
        if (updated.up) {
            fetchProfileData()
            updated.up = false
        }
    }
    fun navigateToHistoryDetail(bundle: Bundle) {
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        viewPager.visibility = View.GONE
        bottomNavigationView.visibility = View.GONE
        val fragment = HistoryDetailFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainframelayout, fragment)
            addToBackStack(null)
            commit()
        }
    }
    private fun fetchProfileData() {
        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken", null)
        thread {
            val url = URL("https://muslimapp.vercel.app/muslims/loggedin")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $access")
                val responseCode = responseCode
                if (responseCode == 200)
                    inputStream.bufferedReader().use {
                        val jsonobject = JSONObject(it.readText())
                        runOnUiThread {
                            val model = ViewModelProvider(this@MainActivity).get(SharedViewModel::class.java)
                            model.setJsonData(jsonobject)
                        }
                    }
            }
        }
    }
    private fun fetchHistoryDates(){
        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken", null)
        thread {
                val url = URL("https://muslimapp.vercel.app/duties/get_history")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $access")
                    if (responseCode == 200) {
                        inputStream.bufferedReader().use {
                            val jsonarray = JSONArray(it.readText()) //important
                            runOnUiThread {
                                val model = ViewModelProvider(this@MainActivity).get(SharedViewModel::class.java)
                                model.setHistory(jsonarray)
                            }
                        }
                    }
                }
            }
    }
    private fun fetchPrayertimes(){
        thread {
            val url = URL("https://api.aladhan.com/v1/timings/17-07-2007?latitude=23.777176&longitude=90.399452&method=1")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                if (responseCode == 200) {
                    inputStream.bufferedReader().use {
                        val jsonobject = JSONObject(it.readText()) //important
                        runOnUiThread {
                            val model = ViewModelProvider(this@MainActivity).get(SharedViewModel::class.java)
                            model.setPrayertimes(jsonobject.getJSONObject("data").getJSONObject("timings"))
                        }
                    }
                }
            }
        }
    }
}