package com.asiradnan.muslimapp

import SharedViewModel
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class PrayerTimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prayer_time)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fajr:TextView = findViewById(R.id.fajr_time)
        val sunrise:TextView = findViewById(R.id.sunrise_time)
        val duhr:TextView = findViewById(R.id.duhr_time)
        val asr:TextView = findViewById(R.id.asr_time)
        val sunset:TextView = findViewById(R.id.sunset_time)
        val maghrib:TextView = findViewById(R.id.maghrib_time)
        val isha:TextView = findViewById(R.id.isha_time)

        fajr.text = "Fajr: " + intent.getStringExtra("Fajr")
        sunrise.text = "Sunrise: " + intent.getStringExtra("Sunrise")
        duhr.text = "Dhuhr: " + intent.getStringExtra("Dhuhr")
        asr.text = "Asr: " + intent.getStringExtra("Asr")
        maghrib.text ="Maghrib: " + intent.getStringExtra("Maghrib")
        sunset.text ="Sunset: " + intent.getStringExtra("Sunset")
        isha.text ="Isha: " + intent.getStringExtra("Isha")

    }
}