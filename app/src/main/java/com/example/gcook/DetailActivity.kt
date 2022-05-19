package com.example.gcook

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Adapter.CommentAdapter
import com.example.gcook.Adapter.MaterialAdapter
import com.example.gcook.Adapter.NewFoodAdapter
import com.example.gcook.Adapter.StepAdapter
import com.example.gcook.Model.*
import com.example.gcook.databinding.ActivityDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Date
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var cmtAdapter: CommentAdapter
    private lateinit var newFoodAdapter: NewFoodAdapter
    private lateinit var comment: Comment
    private lateinit var listComment: ArrayList<Comment>
    private lateinit var uId: String
    private lateinit var foodId: String
    private lateinit var history: History

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        listComment = ArrayList()
        cmtAdapter = CommentAdapter(listComment)
        binding.listCmt.layoutManager = LinearLayoutManager(this@DetailActivity)
        binding.listCmt.adapter = cmtAdapter

        val sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        uId = sharedPref.getString("uId", null).toString()
        foodId = intent.getStringExtra("food_id").toString()

        addHistory()
        loadDetail()
        loadFavorite()
        loadUser()
        loadCmt()

        binding.sendCmt.setOnClickListener {
            sendCmt()
        }

        binding.favorite.setOnClickListener {
            favorite()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }

    private fun favorite() {
        val key = "$uId$foodId"
        database.getReference("favorites").child(key).get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    database.getReference("users")
                        .child(uId)
                        .get()
                        .addOnSuccessListener { user ->
                            database.getReference("foods")
                                .child(foodId)
                                .get()
                                .addOnSuccessListener {
                                    val food = it.getValue(Food::class.java)!!
                                    val user = user.getValue(User::class.java)!!
                                    history = History(id = key, user = user, food = food)
                                    database.getReference("favorites").child(key).setValue(history)
                                    binding.favorite.setImageResource(R.drawable.ic_favorite)
                                }
                        }
                } else {
                    database.getReference("favorites").child(key).removeValue()
                    binding.favorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }
    }

    private fun loadFavorite() {
        val key = "$uId$foodId"
        database.getReference("favorites").child(key).get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    binding.favorite.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    binding.favorite.setImageResource(R.drawable.ic_favorite)
                }
            }
    }

    private fun sendCmt() {
        if(binding.cmtContent.text.toString() != "") {
            val commentData = database.getReference("comments")
            val key = commentData.push().key.toString()
            database.reference.child("foods")
                .child(foodId)
                .get()
                .addOnSuccessListener { food ->
                    database.reference.child("users")
                        .child(uId)
                        .get()
                        .addOnSuccessListener {
                            comment = Comment(
                                id = key,
                                time = System.currentTimeMillis().toString(),
                                content = binding.cmtContent.text.toString(),
                                user = it.getValue(User::class.java)!!,
                                food = food.getValue(Food::class.java)!!
                            )
                            commentData.child(key).setValue(comment)
                                .addOnSuccessListener {
                                    binding.cmtContent.text.clear()
                                }
                        }
                }
        }
    }

    private fun loadUser() {
        database.reference.child("users")
            .child(uId)
            .get()
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it.child("avatarUrl").value.toString())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.avatarCmt)
            }
    }

    private fun loadDetail() {
        database.reference.child("foods")
            .child(foodId)
            .get()
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it.child("imageUrl").value.toString())
                    .into(binding.foodImage)

                binding.foodName.text = it.child("name").value.toString()

                binding.desContent.text = it.child("des").value.toString()

                Glide.with(this)
                    .load(it.child("user").child("avatarUrl").value.toString())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.userAvatar)

                binding.userDisplayname.text = it.child("user").child("displayName").value.toString()

                val listMaterial: ArrayList<Material> = ArrayList()
                for(materialItem in it.child("materials").children) {
                    val material = materialItem.getValue(Material::class.java)
                    listMaterial.add(material!!)
                }
                val materialAdapter = MaterialAdapter(listMaterial)
                binding.materialList.layoutManager = LinearLayoutManager(this)
                binding.materialList.adapter = materialAdapter

                val listStep: ArrayList<String> = ArrayList()
                for(stepItem in it.child("steps").children) {
                    listStep.add(stepItem!!.value.toString())
                }
                val stepAdapter = StepAdapter(listStep)
                binding.stepList.layoutManager = LinearLayoutManager(this)
                binding.stepList.adapter = stepAdapter

                loadNewFood(it.child("user/uid").value.toString())

                binding.foodTime.text =  getDateTime(it.child("timeUpdate").value.toString())

            }
    }

    private fun loadCmt() {
        val foodId = intent.getStringExtra("food_id").toString()
        val query = database.getReference("comments").orderByChild("food/id").equalTo(foodId)
        query.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listComment.clear()
                for(cmtSnapshot in snapshot.children) {
                    val cmt = cmtSnapshot.getValue(Comment::class.java)
                    listComment.add(0, cmt!!)
                }
                cmtAdapter.notifyDataSetChanged()
                binding.cmtTitle.text = "Bình luận (${listComment.size})"
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadNewFood(uId: String) {
        val listNewFood: ArrayList<Food> = ArrayList()
        database.reference.child("users")
            .child(uId)
            .get()
            .addOnSuccessListener {
                binding.newFoodTitle.text = "Món ăn mới của "+it.child("displayName").value.toString()
            }
        val query = database.getReference("foods").orderByChild("user/uid").equalTo(uId).limitToLast(4)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val food = foodSnapshot.getValue(Food::class.java)
                    listNewFood.add(0, food!!)
                }
                newFoodAdapter = NewFoodAdapter(listNewFood)
                binding.listFood.layoutManager = GridLayoutManager(this@DetailActivity, 2)
                binding.listFood.adapter = newFoodAdapter
                newFoodAdapter.onItemClick = {
                    val intent = Intent(this@DetailActivity, DetailActivity::class.java)
                    intent.putExtra("food_id",it)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(s.toLong())
            "Ngày đăng: "+sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun addHistory() {
        val key = "$uId$foodId"
        database.getReference("histories").child(key).get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    database.getReference("users")
                        .child(uId)
                        .get()
                        .addOnSuccessListener { user ->
                            database.getReference("foods")
                                .child(foodId)
                                .get()
                                .addOnSuccessListener {
                                    val food = it.getValue(Food::class.java)!!
                                    val user = user.getValue(User::class.java)!!
                                    history = History(id = key, user = user, food = food)
                                    database.getReference("histories").child(key).setValue(history)
                                }
                        }
                }
            }
    }

}