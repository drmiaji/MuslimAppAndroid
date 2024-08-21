package com.asiradnan.muslimapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class FeedBackFragment : Fragment(R.layout.fragment_feedback) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn: Button = view.findViewById(R.id.button4)
        btn.setOnClickListener {
            Log.d("loggerboi","button clicked")
            val sharedpref = requireContext().getSharedPreferences("authorization", Context.MODE_PRIVATE)
            val access = sharedpref.getString("accesstoken", null)
            if (!access.isNullOrEmpty()) {
                val detailinput: TextView = view.findViewById(R.id.detailinput)
                val numinput: TextView = view.findViewById(R.id.numinput)
                val bookinput: TextView = view.findViewById(R.id.bookinput)
                Log.d("loggerboi","before thread")
                thread {
                    val url = URL("https://muslimapp.vercel.app/duties/feedback")
                    val jsonObject = JSONObject()
                    jsonObject.put("detail", detailinput.text)
                    jsonObject.put("book", bookinput.text)
                    jsonObject.put("number", numinput.text)
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
                            activity?.runOnUiThread {
                                ok()
                            }
                        else
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT)
                                    .show();
                            }
                    }
                }
            }
        }
    }
    private fun ok(){
        val btn:Button? = view?.findViewById(R.id.button4)
        btn?.text = "200";
        Log.d("loggerboi","got 200")
    }
}