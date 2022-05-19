package com.example.gcook

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.databinding.FragmentAddBinding
import com.google.firebase.database.FirebaseDatabase

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var uId: String
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        val homeActivity = activity as HomeActivity

        uId = homeActivity.getId()

        loadWelcome(uId)
        binding.addFood.setOnClickListener {
            startActivity(Intent(activity, AddFoodActivity::class.java))
        }

        return binding.root
    }

    private fun loadWelcome(uId: String) {
        database.reference.child("users")
            .child(uId)
            .get()
            .addOnSuccessListener {
                val welcome = "Xin chào, ${it.child("displayName").value.toString()}\n" +
                        "Hãy chia sẻ công thức nấu ăn của bạn cho mọi người nào!"
                binding.welcome.setText(welcome)
            }
    }

}