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
                    jsonObject.put("username", "$username")
                    jsonObject.put("password", "$password")
                    var postData = jsonObject.toString();
                    Log.d("logger", postData);
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        setRequestProperty(
                            "Content-Type",
                            "application/json"
                        )  // Set content type to JSON
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



        

//        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
//        val accessToken = sharedPreferences.getString("accesstoken", null)
//        if (accessToken.isNullOrEmpty()) redirectToLogin()
//        else {
//            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
//            thread{
//                val url = URL("https://muslimapp.vercel.app/muslims/loggedin")
//                try {
//                    with(url.openConnection() as HttpURLConnection) {
//                        requestMethod = "GET"
//                        setRequestProperty("Authorization", "Bearer $accessToken")
//                        connectTimeout = 5000
//                        readTimeout = 5000
//                        val responseCode = responseCode
//                        Log.d("loggwer", "Token validation response code: $responseCode")
//                        runOnUiThread {
//                            if (responseCode == 200) logoutFun()
//                            else redirectToLogin()
//                        }
//                    }
//                }
//                catch (e: IOException) {
//                    runOnUiThread {
//                        redirectToLogin()
//                    }
//                    Log.e("loggwer", "Error during token validation", e)
//                }
//                catch (e: Exception) {
//                    runOnUiThread {
//                        redirectToLogin()
//                    }
//                    Log.e("loggwer", "Unexpected error during token validation", e)
//                }
//            }
//        }
    }
    fun logoutFun(){
//        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
//        val button: Button = findViewById(R.id.button);
//        val lo: Button = findViewById(R.id.logoutbutton);
//        val usernameInput: TextInputEditText = findViewById(R.id.usernameinput)
//        val passwordInput: TextInputEditText = findViewById(R.id.passwordinput)
//        button.visibility = View.GONE;
//        usernameInput.visibility = View.GONE
//        passwordInput.visibility = View.GONE
//        lo.visibility = View.VISIBLE;
//        lo.setOnClickListener {
//            thread {
//                val url = URL("https://muslimapp.vercel.app/muslims/logout")
//                val jsonObject = JSONObject()
//                val refreshToken = sharedPreferences.getString("refreshtoken", null)
//                jsonObject.put("refresh", "$refreshToken")
//                var postData = jsonObject.toString();
//                Log.d("logger", postData);
//                with(url.openConnection() as HttpURLConnection) {
//                    requestMethod = "POST"
//                    setRequestProperty(
//                        "Content-Type",
//                        "application/json"
//                    )
//                    setRequestProperty("Accept", "application/json")
//                    doOutput = true
//                    OutputStreamWriter(outputStream).apply {
//                        write(postData)
//                        flush()
//                        close()
//                    }
//                    val responseCode = responseCode
//                    Log.d("loggwer", responseCode.toString())
//                    runOnUiThread {
////                        redirectToLogin()
//                    }
//                }
//            }
//        }
    }
    fun goBackToProfile(){
        startActivity(Intent(this,ProfileActivity::class.java));
    }
}
