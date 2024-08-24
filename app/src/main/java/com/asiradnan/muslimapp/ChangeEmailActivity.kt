package com.asiradnan.muslimapp

import SharedViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern
import kotlin.concurrent.thread

class ChangeEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_email)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btn:Button = findViewById(R.id.changeemailconfirm)
        btn.setOnClickListener {
            val txt:TextView = findViewById(R.id.changeemailinput)
            val email = txt.text
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
                val access = sharedPreferences.getString("accesstoken",null)
                if (access.isNullOrEmpty()) startActivity(Intent(this,LoginActivity::class.java))
                else{
                    thread{
                        val url = URL("https://muslimapp.vercel.app/muslims/changeemail")
                        val jsonObject = JSONObject()
                        jsonObject.put("email", email)
                        val postData = jsonObject.toString()
                        with (url.openConnection() as HttpURLConnection){
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
                                        Toast.makeText(this@ChangeEmailActivity, "Email Updated!",Toast.LENGTH_SHORT).show()
                                        fetch()
                                    }
                                else runOnUiThread {
                                        Toast.makeText(this@ChangeEmailActivity, "Try again!", Toast.LENGTH_SHORT).show()
                                    }
                        }
                    }
                }
            }
            else Toast.makeText(this@ChangeEmailActivity, "Enter a valid Email", Toast.LENGTH_SHORT).show()

        }
    }
    private fun fetch(){
        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val access = sharedPreferences.getString("accesstoken", null)
        thread {
            val url = URL("https://muslimapp.vercel.app/muslims/loggedin")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $access")
                val responseCode = responseCode
                if (responseCode == 200)
                    inputStream.bufferedReader().use {
                        val jsonobject = JSONObject(it.readText())
                        runOnUiThread {
                            val model = ViewModelProvider(application as MainActivity).get(SharedViewModel::class.java)
                            model.setJsonData(jsonobject)
                            val resultIntent = Intent()
                            resultIntent.putExtra("FRAGMENT_INDEX", 3)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }
            }
        }
    }
}