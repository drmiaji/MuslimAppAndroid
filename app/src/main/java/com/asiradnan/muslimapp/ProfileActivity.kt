package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        show(2);
        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accesstoken", null)
        if (accessToken.isNullOrEmpty()) showOptions()
        else {
            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
            thread {
                val url = URL("https://muslimapp.vercel.app/muslims/loggedin")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $accessToken")
                    val responseCode = responseCode
                    Log.d("loggerboi","$responseCode")
                    if (responseCode == 200)
                        inputStream.bufferedReader().use {
                            Log.d("loggerboi","We are hereeeeeeeeee1")
                            val jsonobject:JSONObject = JSONObject(it.readText());
                            runOnUiThread {
                                showProfile(jsonobject);
                            }
                        }
                    else {
                        runOnUiThread {
                            showOptions();
                        }
                    }
                }
            }
        }
    }
    private fun showProfile(response:JSONObject){
        show(0);

        val name:TextView = findViewById(R.id.name);
        val age:TextView = findViewById(R.id.age);
        val marital:TextView = findViewById(R.id.marital);
        val gender:TextView = findViewById(R.id.gender);
        val username:TextView = findViewById(R.id.username);
        val email:TextView = findViewById(R.id.email)
        val logoutbutton:Button = findViewById(R.id.logoutbutton)

        name.text = response.optString("first_name") + " " + response.optString("last_name")
        age.text = response.optString("age")
        Log.d("loggerboi",response.optString("is_male"));
        if (response.optString("is_male")=="true") gender.text = "Male"
        else gender.text = "Female"
        if (response.optString("is_married")=="true") marital.text = "Married"
        else marital.text = "Unmarried"
        username.text = response.optString("username")
        email.text = response.optString("email")

        logoutbutton.setOnClickListener{
            logOut();
        }
    }
    private fun showOptions(){
        show(1);

        val loginbutton:Button = findViewById(R.id.loginbutton);
        val signupbutton:Button = findViewById(R.id.signupbutton)
        makeVisible(loginbutton);
        makeVisible(signupbutton);

        loginbutton.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }
        signupbutton.setOnClickListener{
            startActivity(Intent(this,SignupActivity::class.java))
        }

    }
    private fun logOut(){
        val sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE)
        thread {
            val url = URL("https://muslimapp.vercel.app/muslims/logout")
            val jsonObject = JSONObject()
            val refreshToken = sharedPreferences.getString("refreshtoken", null)
            jsonObject.put("refresh", "$refreshToken")
            var postData = jsonObject.toString();
            Log.d("logger", postData);
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
                runOnUiThread {
                    val editor = sharedPreferences.edit()
                    editor.putString("accesstoken", null)
                    editor.apply()
                    showOptions();
                }
            }
        }
    }
    private fun makeInvisible(x:View){
        x.visibility = View.GONE;
    }
    private fun makeVisible(x:View){
        x.visibility = View.VISIBLE;
    }
    private fun show(flag:Int) {
        val name: TextView = findViewById(R.id.name);
        val age: TextView = findViewById(R.id.age);
        val marital: TextView = findViewById(R.id.marital);
        val gender: TextView = findViewById(R.id.gender);
        val username: TextView = findViewById(R.id.username);
        val email: TextView = findViewById(R.id.email)
        val namelabel: TextView = findViewById(R.id.namelabel);
        val agelabel: TextView = findViewById(R.id.agelabel);
        val maritallabel: TextView = findViewById(R.id.maritallabel);
        val genderlabel: TextView = findViewById(R.id.genderlabel);
        val usernamelabel: TextView = findViewById(R.id.usernamelabel);
        val emaillabel: TextView = findViewById(R.id.emaillabel)
        val profileheader: TextView = findViewById(R.id.profileheader)
        val logoutbutton: Button = findViewById(R.id.logoutbutton)
        val loginbutton: Button = findViewById(R.id.loginbutton);
        val signupbutton: Button = findViewById(R.id.signupbutton)
        if (flag == 0) {
            makeInvisible(loginbutton);
            makeInvisible(signupbutton);

            makeVisible(name);
            makeVisible(age);
            makeVisible(marital);
            makeVisible(gender);
            makeVisible(username);
            makeVisible(email);
            makeVisible(namelabel);
            makeVisible(agelabel);
            makeVisible(maritallabel);
            makeVisible(genderlabel);
            makeVisible(usernamelabel);
            makeVisible(emaillabel);
            makeVisible(logoutbutton);
            makeVisible(profileheader);
        } else if (flag == 1) {
            makeInvisible(name);
            makeInvisible(age);
            makeInvisible(marital);
            makeInvisible(gender);
            makeInvisible(username);
            makeInvisible(email);
            makeInvisible(namelabel);
            makeInvisible(agelabel);
            makeInvisible(maritallabel);
            makeInvisible(genderlabel);
            makeInvisible(usernamelabel);
            makeInvisible(emaillabel);
            makeInvisible(logoutbutton);
            makeInvisible(profileheader);

            makeVisible(loginbutton);
            makeVisible(signupbutton);
        } else {
            makeInvisible(name);
            makeInvisible(age);
            makeInvisible(marital);
            makeInvisible(gender);
            makeInvisible(username);
            makeInvisible(email);
            makeInvisible(namelabel);
            makeInvisible(agelabel);
            makeInvisible(maritallabel);
            makeInvisible(genderlabel);
            makeInvisible(usernamelabel);
            makeInvisible(emaillabel);
            makeInvisible(logoutbutton);
            makeInvisible(profileheader);
            makeInvisible(loginbutton);
            makeInvisible(signupbutton);
        }
    }
}