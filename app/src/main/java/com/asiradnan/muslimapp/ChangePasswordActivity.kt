package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btn: Button = findViewById(R.id.changepasswordconfirm)
        btn.setOnClickListener {
            val txt: TextView = findViewById(R.id.changepasswordinput)
            val password = txt.text
            val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
            val access = sharedPreferences.getString("accesstoken", null)
            if (access.isNullOrEmpty()) startActivity(Intent(this, LoginActivity::class.java))
            else {
                thread {
                    val url = URL("https://muslimapp.vercel.app/muslims/changepassword")
                    val jsonObject = JSONObject()
                    jsonObject.put("password", password)
                    val postData = jsonObject.toString()
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        setRequestProperty("Authorization", "Bearer $access")
                        setRequestProperty(
                            "Content-Type",
                            "application/json"
                        )
                        setRequestProperty("Accept", "application/json")
                        doOutput = true
                        OutputStreamWriter(outputStream).apply {
                            write(postData)
                            flush()
                            close()
                        }
                        if (responseCode == 200)
                            runOnUiThread {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "Password Updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        else runOnUiThread {
                                Toast.makeText(this@ChangePasswordActivity, "Try again!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

        }
    }
}