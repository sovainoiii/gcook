package com.example.gcook.UI.Home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gcook.*
import com.example.gcook.UI.Auth.SignInActivity
import com.example.gcook.UI.Home.Fragment.AddFragment
import com.example.gcook.UI.Home.Fragment.HomeFragment
import com.example.gcook.UI.Home.Fragment.SearchFragment
import com.example.gcook.UI.Home.Fragment.UserFragment
import com.example.gcook.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var uId: String
    private lateinit var sharedPref: SharedPreferences
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        val id = sharedPref.getString("uId", null)
        uId = id.toString()

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.add -> replaceFragment(AddFragment())
                R.id.user -> replaceFragment(UserFragment())
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
        }
    }

    fun getId(): String {
        return uId
    }

    fun logout() {
        val editor = sharedPref.edit()
        editor.remove("uId").apply()
        gsc.signOut()
            .addOnCompleteListener(this) {
                startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
            }
    }

}