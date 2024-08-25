package com.asiradnan.muslimapp

import android.icu.util.IslamicCalendar
import android.icu.util.ULocale
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val calendarView: CalendarView = findViewById(R.id.calendar)
        val islamicDateTextView: TextView = findViewById(R.id.islamicDateTextView)

        // Set up the Islamic calendar
        val islamicCalendar = IslamicCalendar.getInstance(ULocale("en@calendar=islamic"))

        // Display the initial Islamic date
//        updateIslamicDateTextView(islamicCalendar, islamicDateTextView)

        // Set up a listener to update the Islamic date when the user changes the date in CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Set the Gregorian date on the Islamic calendar
            val islamic = IslamicCalendar.getInstance(ULocale("en@calendar=islamic"))
            islamic.set(year, month, dayOfMonth)


//            updateIslamicDateTextView(islamicCalendar, islamicDateTextView)
        }
    }
//    private fun updateIslamicDateTextView(islamicCalendar: Calendar, textView: TextView) {
//        val day = islamicCalendar.get(Calendar.DAY_OF_MONTH)
//    val monthIndex = islamicCalendar.get(Calendar.MONTH)
//    val year = islamicCalendar.get(Calendar.YEAR)
//
//    // Manually map the month index to the month name
//    val monthNames = arrayOf("Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
//                             "Jumada al-awwal", "Jumada al-thani", "Rajab",
//                             "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah")
//    val month = monthNames[monthIndex]
//
//    val islamicDate = "$day $month $year"
//    textView.text = islamicDate
//    }

}
