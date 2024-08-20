package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.menu_item_profile;
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_item_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.menu_item_profile ->true
                else -> false
            }
        }

        val button: Button = findViewById(R.id.button);
        val usernameInput: TextInputEditText = findViewById(R.id.usernameinput)
        val passwordInput: EditText = findViewById(R.id.passwordinput)
        button.setOnClickListener {
            try {
                val username = usernameInput.text?.toString() ?: ""
                val password = passwordInput.text?.toString() ?: ""
                thread {
                    val url = URL("https://muslimapp.vercel.app/muslims/login")
                    val jsonObject = JSONObject()
                    jsonObject.put("username", username)
                    jsonObject.put("password", password)
                    val postData = jsonObject.toString();
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        setRequestProperty(
                            "Content-Type",
                            "application/json"
                        )
                        setRequestProperty("Accept", "application/json")
                        doOutput = true  // Enable output stream
                        OutputStreamWriter(outputStream).apply {
                            write(postData)
                            flush()
                            close()
                        }
                        val responseCode = responseCode
                        if (responseCode ==200) {
                            inputStream.bufferedReader().use {
                                val response = it.readText()
                                val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                val responseJson = JSONObject(response)
                                val accessToken = responseJson.optString("access")
                                val refreshToken = responseJson.optString("refresh")
                                editor.putString("accesstoken", accessToken)
                                editor.putString("refreshtoken", refreshToken)
                                editor.apply()
                            }
                            runOnUiThread {
                                goBackToProfile();
                            }
                        }
                        else runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            catch (e: Exception) {
                Log.e("logger", "Error: ${e.message}")
            }
        }
    }
    fun goBackToProfile(){
        startActivity(Intent(this,ProfileActivity::class.java));
    }
}
