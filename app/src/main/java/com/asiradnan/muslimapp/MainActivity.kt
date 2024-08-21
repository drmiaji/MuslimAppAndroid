package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        val loggedin = sharedpref.getString("loggedin", null)
        if (!loggedin.isNullOrEmpty()) {
            val dailydutiesfrag= DailyDutiesFragment()
            val historyfrag = HistoryFragment()
            val profilefrag = ProfileFragment()
            val feedbackfrag = FeedBackFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.mainframelayout, dailydutiesfrag)
                commit()
            }
            val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_item_home -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.mainframelayout, dailydutiesfrag)
                            commit()
                        }
                        true
                    }

                    R.id.menu_item_history -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.mainframelayout, historyfrag)
                            commit()
                        }
                        true
                    }

                    R.id.menu_item_feedback -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.mainframelayout, feedbackfrag)
                            commit()
                        }
                        true
                    }

                    R.id.menu_item_profile -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.mainframelayout, profilefrag)
                            commit()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
        else startActivity(Intent(this, LoginActivity::class.java))
    }
}