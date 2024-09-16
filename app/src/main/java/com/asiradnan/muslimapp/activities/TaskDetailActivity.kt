package com.asiradnan.muslimapp.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asiradnan.muslimapp.R
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var jsobobject:JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val title = intent.getStringExtra("title")
        val id = intent.getStringExtra("id")
        val detail = intent.getStringExtra("detail")
        val header: TextView? = findViewById(R.id.task_detail_header)
        val detailview: TextView? = findViewById(R.id.task_detail_detail)
        header?.text = title
        detailview?.text = detail
        if (id != null) {
            Log.d("loggerboi",id)
        }
        id?.toInt()?.let { ref(it) }

    }
    private fun ref(id:Int){
        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken", null)
        thread {
                val url = URL("https://muslim.asiradnan.com/duties/task_ref/$id")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $access")
                    if (responseCode == 200) {
                        inputStream.bufferedReader().use {
                            jsobobject = JSONObject(it.readText()) //important
                            runOnUiThread {
                                upd(jsobobject)
                            }
                        }
                    }
                }
            }
    }
    private fun upd(jsobobject:JSONObject){
        val v:TextView = findViewById(R.id.ref)
        val qr = jsobobject.getJSONArray("hadith_references")
        for (i in 0 until qr.length()) {
            v.text = v.text.toString() + "\n" + (i+1) + ". " + qr.getJSONObject(i).getString("hadith")
        }
    }
}