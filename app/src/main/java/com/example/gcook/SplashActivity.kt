package com.example.gcook

import android.app.Activity
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.Toast
import com.example.gcook.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        val id = sharedPref.getString("uId", null)
        if(id != null) {
            changeIntent(HomeActivity())
        } else {
            changeIntent(SignInActivity())
        }
    }
    private fun changeIntent(activity: Activity) {
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, activity::class.java)
            startActivity(intent)
        }, 2000)
    }

}