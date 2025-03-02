package com.asiradnan.muslimapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asiradnan.muslimapp.R
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

            if (username.text.isEmpty())  {
                Toast.makeText(this,"Enter Username",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.text.length < 6)  {
                Toast.makeText(this,"Enter at least 6 characters password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (age.text.isEmpty())  {
                Toast.makeText(this,"Enter Age",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.text.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email.text).matches()){
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
                Toast.makeText(this,"Please wait!",Toast.LENGTH_SHORT).show()
                thread {
                    val url = URL("https://muslim.asiradnan.com/muslims/register")
                    val postData = data.toString()
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
                        runOnUiThread {
                                Log.d("loggerboi", responseCode.toString())
                            }
                        if (responseCode == 200) {
                            runOnUiThread {
                                sendToLogIn()
                            }
                        }
                        else if (responseCode == 409) {
                            runOnUiThread {
                                Toast.makeText(this@SignupActivity,"Username already taken!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            else Toast.makeText(this,"Enter a valid Email",Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendToLogIn(){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}