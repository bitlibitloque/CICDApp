package org.example.cicdapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class MainActivity : AppCompatActivity(), View.OnClickListener{

    //TODO get your apiKey from https://appcenter.ms/
    // you only need register, is free
    // also look for the word APP CENTER in the project, you have to change:
    // build.gradle (project)-> Change gradle version to 4.1.1
    // build.gradle (app) ->Add some dependencies
    //private val apyKey = "XXXXXXXX_GET_YOUR_API_KEY_XXXXXXXXX"
    private val apyKey = "7cca02ce-e827-4ea9-9b81-0554fa573d96"
    private var tvMainMsg: TextView? = null
    private var btnMain1: Button? = null
    private var btnMain2: Button? = null
    private var btnMain3: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
        showMsg(resources.getString(R.string.app_intro))

        initAppCenter()
        showMsgIfAppCrashInLastSession()
    }

    private fun initAppCenter() {
        AppCenter.start(application, apyKey, Analytics::class.java, Crashes::class.java)
    }

    private fun showMsgIfAppCrashInLastSession() {
        //IF THE APP CRASH IN LAS SESSION, WE CAN SHOW A MESSAGE
        val future = Crashes.hasCrashedInLastSession()
        future.thenAccept {
            if (it) {
                showMsg("Sorry for last session crash!")
            }
        }
    }

    private fun initUi() {
        tvMainMsg = findViewById(R.id.tv_main_msg)

        btnMain1 = findViewById(R.id.btn_main_1)
        btnMain2 = findViewById(R.id.btn_main_2)
        btnMain3 = findViewById(R.id.btn_main_3)

        btnMain1?.setOnClickListener(this)
        btnMain2?.setOnClickListener(this)
        btnMain3?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_main_1 -> throw Exception("Our crash with Exception!!")
            R.id.btn_main_2 -> Crashes.generateTestCrash() // show info in AppCenter console
            R.id.btn_main_3 -> trackEventWithProperties()
        }
    }

    private fun showMsg(message: String) {
        tvMainMsg?.text = message
    }

    private fun trackEventWithProperties() {
        try {
            val properties: HashMap<String, String> = HashMap()

            showMsg("My name is DemoCI")
            val name = tvMainMsg?.text.toString()
            // Add others
            val age = 16 // editTextAge.text.toString.toInt

            if (name != null) {
                properties["userName"] = name
            }
            properties["userAge"] = age.toString()
            properties["wifiState"] = "on"
            properties["location"] = "Earth"
            properties["other"] = "other track if I want"
            // Add others

            Analytics.trackEvent("Click track event: ", properties)
        } catch (e: Exception) {
            Analytics.trackEvent(e.message)
        }
    }
}
