package com.example.gcook.UI.Home.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Adapter.ViewPagerAdapter
import com.example.gcook.UI.Profile.ProfileActivity
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.FragmentUserBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.FirebaseDatabase

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var uId: String
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity
        uId = homeActivity.getId()

        viewPagerAdapter = ViewPagerAdapter(homeActivity.supportFragmentManager, lifecycle)
        binding.menu.adapter = viewPagerAdapter

        TabLayoutMediator(binding.menuTab, binding.menu) {tab,position ->
            when(position) {
                0 -> {
                    tab.text = "Món của tôi"
                }
                1 -> {
                    tab.text = "Món yêu thích"
                }
            }
        }.attach()

        loadUser()

        binding.logout.setOnClickListener {
            homeActivity.logout()
        }

        return binding.root
    }

    private fun loadUser() {
        database.reference.child("users")
            .child(uId)
            .get()
            .addOnSuccessListener {

                Glide.with(this)
                    .load(it.child("avatarUrl").value.toString())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.avatar)

                binding.displayName.text = it.child("displayName").value.toString()

                binding.email.text = it.child("email").value.toString()

                binding.profile.setOnClickListener {
                    val intent = Intent(activity, ProfileActivity::class.java)
                    intent.putExtra("uId", uId)
                    startActivity(intent)
                }

                if(it.child("rule").value.toString() == "admin") {
                    binding.admin.visibility = View.VISIBLE
                }

            }
    }

}