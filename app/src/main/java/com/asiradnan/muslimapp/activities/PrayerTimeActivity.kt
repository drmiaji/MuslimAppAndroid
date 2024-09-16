package com.asiradnan.muslimapp.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asiradnan.muslimapp.R
import java.text.SimpleDateFormat
import java.util.Locale

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


        fajr.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Fajr"))!!)
        sunrise.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Sunrise"))!!)
        duhr.text =SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Dhuhr"))!!)
        asr.text =SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Asr"))!!)
        maghrib.text =SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Maghrib"))!!)
        sunset.text =SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Sunset"))!!)
        isha.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.getDefault()).parse(intent.getStringExtra("Isha"))!!)
    }
}