package com.asiradnan.muslimapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class UpdateProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        intent.getStringExtra("first_name")
        intent.getStringExtra("last_name")





        val firstname: EditText = findViewById(R.id.name)
        val lastname: EditText = findViewById(R.id.lastnameinput)
        val age: EditText = findViewById(R.id.age)
        val email:EditText = findViewById(R.id.email)
        val is_male: RadioButton = findViewById(R.id.maleRadioButton)
        val is_married: RadioButton = findViewById(R.id.marriedRadioButton)
        val is_female: RadioButton = findViewById(R.id.femaleRadioButton)
        val is_unmarried: RadioButton = findViewById(R.id.unmarriedRadioButton)

        age.setText(intent.getStringExtra("age"))
        if (intent.getStringExtra("marital")=="Married") is_married.isChecked = true
        else is_unmarried.isChecked = true
        if (intent.getStringExtra("gender")=="Male") is_male.isChecked = true
        else is_female.isChecked = true
        email.setText(intent.getStringExtra("email"))
        firstname.setText(intent.getStringExtra("first_name"))
        lastname.setText(intent.getStringExtra("last_name"))
        val btn:Button = findViewById(R.id.button6)
        btn.setOnClickListener {
            val sharedpref = getSharedPreferences("authorization", Context.MODE_PRIVATE)
            val access = sharedpref.getString("accesstoken",null)
            val data = JSONObject()
            data.put("first_name", firstname.text)
            data.put("last_name", lastname.text)
            data.put("email", email.text)
            data.put("age", age.text)
            if (is_male.isChecked) data.put("is_male", "True")
            else data.put("is_male", "False")
            if (is_married.isChecked) data.put("is_married", "True")
            else data.put("is_married", "False")
            thread {
                val url = URL("https://muslimapp.vercel.app/muslims/updateprofile")
                val postData = data.toString()
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Authorization","Bearer $access")
                    setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )

                    doOutput = true
                    OutputStreamWriter(outputStream).apply {
                        write(postData)
                        flush()
                        close()
                    }
                    val responseCode = responseCode
                    if (responseCode == 200) {
                        runOnUiThread {
                            val resultIntent = Intent()
                            resultIntent.putExtra("FRAGMENT_INDEX", 3)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }
                    else runOnUiThread {
                            Log.d("loggerboi","We are here $responseCode")

                        }
                }
            }
        }

    }
}