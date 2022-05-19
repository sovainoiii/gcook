package com.example.gcook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.databinding.FragmentAddBinding
import com.example.gcook.databinding.FragmentUserBinding
import com.google.firebase.database.FirebaseDatabase

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var uId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity
        uId = homeActivity.getId()

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
            }
    }

}