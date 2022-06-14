package com.example.gcook.UI.Profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Adapter.FoodProfileAdapter
import com.example.gcook.Model.Food
import com.example.gcook.Model.User
import com.example.gcook.R
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.databinding.ActivityProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var uId: String
    private lateinit var listFood: ArrayList<Food>
    private lateinit var foodAdapter: FoodProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uId = intent.getStringExtra("uId").toString()

        listFood = ArrayList()
        foodAdapter = FoodProfileAdapter(listFood)
        foodAdapter.onItemClick = {
            val intent = Intent(this@ProfileActivity, DetailActivity::class.java)
            intent.putExtra("food_id", it)
            startActivity(intent)
        }
        binding.listFood.layoutManager = GridLayoutManager(this, 2)
        binding.listFood.adapter = foodAdapter

        binding.backBtn.setOnClickListener {
            finish()
        }

        loadUser()

    }

    private fun loadUser() {
        database.getReference("users").child(uId).get()
            .addOnSuccessListener {
                val user = it.getValue(User::class.java)
                Glide.with(this).load(user!!.avatarUrl).apply(RequestOptions.circleCropTransform()).into(binding.avatar)
                binding.username.text = user.displayName
                binding.email.text = user.email
                binding.foodTitle.text = "Món ăn của " + user.displayName
            }
        val query = database.getReference("foods").orderByChild("name")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listFood.clear()
                for(foodSnapshot in snapshot.children) {
                    val food = foodSnapshot.getValue(Food::class.java)
                    if(food!!.user.uId == uId) {
                        listFood.add(food)
                    }
                }
                foodAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}