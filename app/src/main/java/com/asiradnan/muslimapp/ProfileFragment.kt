package com.asiradnan.muslimapp

import SharedViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onStart() {
        super.onStart()
        Log.d("loggerboi","Inside profilefragment onstart")
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.jsonData.observe(viewLifecycleOwner) { jsonObject ->
            showProfile(jsonObject)
        }
    }

    private fun showProfile(response:JSONObject){
        val name:TextView? = view?.findViewById(R.id.name)
        val age: TextView? = view?.findViewById(R.id.age)
        val marital:TextView? = view?.findViewById(R.id.marital)
        val gender:TextView? = view?.findViewById(R.id.gender)
        val username:TextView? = view?.findViewById(R.id.username)
        val email:TextView? = view?.findViewById(R.id.email)
        val logoutbutton: Button? = view?.findViewById(R.id.logoutbutton)
        val changeemail: Button? = view?.findViewById(R.id.changeemail)
        val changepassword: Button? = view?.findViewById(R.id.changepassword)
        val updateprofile: Button? = view?.findViewById(R.id.updateprofile)

        name?.text = response.optString("first_name") + " " + response.optString("last_name")
        age?.text = response.optString("age")
        if (response.optString("is_male")=="true") gender?.text = "Male"
        else gender?.text = "Female"
        if (response.optString("is_married")=="true") marital?.text = "Married"
        else marital?.text = "Unmarried"
        username?.text = response.optString("username")
        email?.text = response.optString("email")

        logoutbutton?.setOnClickListener{
            logOut()
        }
        changeemail?.setOnClickListener{
            startActivity(Intent(requireContext(),ChangeEmailActivity::class.java))
        }
        changepassword?.setOnClickListener {
            startActivity(Intent(requireContext(),ChangePasswordActivity::class.java))
        }
        updateprofile?.setOnClickListener {
            val intent = Intent(requireContext(),UpdateProfileActivity::class.java)
            intent.putExtra("first_name",response.optString("first_name"))
            intent.putExtra("last_name",response.optString("last_name"))
            intent.putExtra("gender","${gender?.text}")
            intent.putExtra("marital","${marital?.text}")
            intent.putExtra("email","${email?.text}")
            intent.putExtra("age","${age?.text}")
            startActivity(intent)
        }
    }
    private fun logOut(){
        val sharedPreferences = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        thread {
            val url = URL("https://muslimapp.vercel.app/muslims/logout")
            val jsonObject = JSONObject()
            val refreshToken = sharedPreferences.getString("refreshtoken", null)
            jsonObject.put("refresh", "$refreshToken")
            val postData = jsonObject.toString()
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
                activity?.runOnUiThread {
                    val editor = sharedPreferences.edit()
                    editor.putString("accesstoken", null)
                    editor.putString("loggedin", null)
                    editor.apply()
                    goBack()
                }
            }
        }
    }
    private fun goBack(){
        startActivity(Intent(requireContext(),LoginActivity::class.java))
    }
}