package com.asiradnan.muslimapp

import android.content.Intent
import android.os.Bundle
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

        val dd= DailyDutiesFragment()
        val historyfrag = HistoryFragment()
        val profilefrag = ProfileFragment()
        val feedbackfrag = FeedBackFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainframelayout, dd)
            commit()
        }
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.mainframelayout, dd)
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
}