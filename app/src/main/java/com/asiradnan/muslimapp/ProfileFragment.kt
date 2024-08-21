package com.asiradnan.muslimapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        show(2)
        val sharedPreferences = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accesstoken", null)
        if (accessToken.isNullOrEmpty()) showOptions()
        else {
            Toast.makeText(requireContext(), "Please wait", Toast.LENGTH_SHORT).show()
            thread {
                val url = URL("https://muslimapp.vercel.app/muslims/loggedin")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $accessToken")
                    val responseCode = responseCode
                    if (responseCode == 200)
                        inputStream.bufferedReader().use {
                            val jsonobject = JSONObject(it.readText())
                            activity?.runOnUiThread {
                                showProfile(jsonobject)
                            }
                        }
                    else {
                        activity?.runOnUiThread {
                            showOptions()
                        }
                    }
                }
            }
        }

    }
    private fun showProfile(response:JSONObject){
        show(0)

        val name:TextView? = view?.findViewById(R.id.name)
        val age: TextView? = view?.findViewById(R.id.age)
        val marital:TextView? = view?.findViewById(R.id.marital)
        val gender:TextView? = view?.findViewById(R.id.gender)
        val username:TextView? = view?.findViewById(R.id.username)
        val email:TextView? = view?.findViewById(R.id.email)
        val logoutbutton: Button? = view?.findViewById(R.id.logoutbutton)

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
    }
    private fun showOptions(){
        show(1)

        val loginbutton:Button? = view?.findViewById(R.id.loginbutton)
        val signupbutton:Button? = view?.findViewById(R.id.signupbutton)
        if (loginbutton != null) {
            makeVisible(loginbutton)
        }
        if (signupbutton != null) {
            makeVisible(signupbutton)
        }

        loginbutton?.setOnClickListener{
            startActivity(Intent(requireContext(),LoginActivity::class.java))
        }
        signupbutton?.setOnClickListener{
            startActivity(Intent(requireContext(),SignupActivity::class.java))
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
    private fun makeInvisible(x:View?){
        x?.visibility = View.GONE
    }
    private fun makeVisible(x:View?){
        x?.visibility = View.VISIBLE
    }
    private fun show(flag:Int) {
        val name: TextView? = view?.findViewById(R.id.name)
        val age: TextView? = view?.findViewById(R.id.age)
        val marital: TextView? = view?.findViewById(R.id.marital)
        val gender: TextView? = view?.findViewById(R.id.gender)
        val username: TextView? = view?.findViewById(R.id.username)
        val email: TextView? = view?.findViewById(R.id.email)
        val namelabel: TextView? = view?.findViewById(R.id.namelabel)
        val agelabel: TextView? = view?.findViewById(R.id.agelabel)
        val maritallabel: TextView? = view?.findViewById(R.id.maritallabel)
        val genderlabel: TextView? = view?.findViewById(R.id.genderlabel)
        val usernamelabel: TextView? = view?.findViewById(R.id.usernamelabel)
        val emaillabel: TextView? = view?.findViewById(R.id.emaillabel)
        val profileheader: TextView? = view?.findViewById(R.id.profileheader)
        val logoutbutton: Button? = view?.findViewById(R.id.logoutbutton)
        val loginbutton: Button? = view?.findViewById(R.id.loginbutton)
        val signupbutton: Button? = view?.findViewById(R.id.signupbutton)
        if (flag == 0) {
            makeInvisible(loginbutton)
            makeInvisible(signupbutton)

            makeVisible(name)
            makeVisible(age)
            makeVisible(marital)
            makeVisible(gender)
            makeVisible(username)
            makeVisible(email)
            makeVisible(namelabel)
            makeVisible(agelabel)
            makeVisible(maritallabel)
            makeVisible(genderlabel)
            makeVisible(usernamelabel)
            makeVisible(emaillabel)
            makeVisible(logoutbutton)
            makeVisible(profileheader)
        } else if (flag == 1) {
            makeInvisible(name)
            makeInvisible(age)
            makeInvisible(marital)
            makeInvisible(gender)
            makeInvisible(username)
            makeInvisible(email)
            makeInvisible(namelabel)
            makeInvisible(agelabel)
            makeInvisible(maritallabel)
            makeInvisible(genderlabel)
            makeInvisible(usernamelabel)
            makeInvisible(emaillabel)
            makeInvisible(logoutbutton)
            makeInvisible(profileheader)

            makeVisible(loginbutton)
            makeVisible(signupbutton)
        } else {
            makeInvisible(name)
            makeInvisible(age)
            makeInvisible(marital)
            makeInvisible(gender)
            makeInvisible(username)
            makeInvisible(email)
            makeInvisible(namelabel)
            makeInvisible(agelabel)
            makeInvisible(maritallabel)
            makeInvisible(genderlabel)
            makeInvisible(usernamelabel)
            makeInvisible(emaillabel)
            makeInvisible(logoutbutton)
            makeInvisible(profileheader)
            makeInvisible(loginbutton)
            makeInvisible(signupbutton)
        }
    }
}