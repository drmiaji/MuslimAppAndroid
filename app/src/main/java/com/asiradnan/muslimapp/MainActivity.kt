package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                        viewPager.currentItem = 0
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
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun navigateToTaskDetail(bundle: Bundle) {
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        viewPager.visibility = View.GONE
        bottomNavigationView.visibility = View.GONE
        val fragment = TaskDetailFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply(){
            replace(R.id.mainframelayout,fragment)
            addToBackStack(null)
            commit()
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
}