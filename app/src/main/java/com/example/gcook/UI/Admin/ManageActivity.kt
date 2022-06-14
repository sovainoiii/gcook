package com.example.gcook.UI.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gcook.R
import com.example.gcook.UI.Admin.Fragments.ManageCommentFragment
import com.example.gcook.UI.Admin.Fragments.ManageFoodFragment
import com.example.gcook.UI.Admin.Fragments.ManageReportFragment
import com.example.gcook.UI.Admin.Fragments.ManageUserFragment
import com.example.gcook.UI.Home.Fragment.AddFragment
import com.example.gcook.UI.Home.Fragment.HomeFragment
import com.example.gcook.UI.Home.Fragment.SearchFragment
import com.example.gcook.UI.Home.Fragment.UserFragment
import com.example.gcook.databinding.ActivityManageBinding

class ManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityManageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        replaceFragment(ManageFoodFragment())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.food -> replaceFragment(ManageFoodFragment())
                R.id.comment -> replaceFragment(ManageCommentFragment())
                R.id.report -> replaceFragment(ManageReportFragment())
                R.id.user -> replaceFragment(ManageUserFragment())
            }

            return@setOnNavigationItemSelectedListener true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
        }
    }
}