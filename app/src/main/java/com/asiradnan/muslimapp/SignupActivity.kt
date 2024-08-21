package com.asiradnan.muslimapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.menu_item_profile
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

        val signup: Button = findViewById(R.id.signup)
        signup.setOnClickListener {
            val firstname: EditText = findViewById(R.id.firstnameinput)
            val lastname: EditText = findViewById(R.id.lastnameinput)
            val username: EditText = findViewById(R.id.usernameinput)
            val password: EditText = findViewById(R.id.passwordinput)
            val email: EditText = findViewById(R.id.emailinput)
            val age: EditText = findViewById(R.id.ageinput)
            val is_male: RadioButton = findViewById(R.id.maleRadioButton)
            val is_married: RadioButton = findViewById(R.id.marriedRadioButton)

            val data = JSONObject()
            data.put("first_name", firstname.text)
            data.put("last_name", lastname.text)
            data.put("username", username.text)
            data.put("password", password.text)
            data.put("email", email.text)
            data.put("age", age.text)
            if (is_male.isChecked) data.put("is_male", "True")
            else data.put("is_male", "False")
            if (is_married.isChecked) data.put("is_married", "True")
            else data.put("is_married", "False")

            val postData = data.toString()
            Log.d("loggerboi", postData)
            thread {
                val url = URL("https://muslimapp.vercel.app/muslims/register")
                val postData = data.toString()
                Log.d("loggerboi", postData)
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
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
                    val responseCode = responseCode
                    Log.d("loggerboi", responseCode.toString())
                    if (responseCode == 200) {
                        runOnUiThread {
                            sendToLogIn()
                        }
                    }
                }
            }
        }
    }
    private fun sendToLogIn(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
}