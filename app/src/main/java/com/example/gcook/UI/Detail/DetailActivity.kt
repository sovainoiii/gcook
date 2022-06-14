package com.example.gcook.UI.Detail

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Adapter.*
import com.example.gcook.Model.*
import com.example.gcook.R
import com.example.gcook.UI.Food.EditFoodActivity
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.UI.Profile.ProfileActivity
import com.example.gcook.databinding.ActivityDetailBinding
import com.google.firebase.database.*
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
        database.getReference("favorites/$uId").child(foodId).get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    database.getReference("favorites/$uId").child(foodId).setValue(foodId)
                    binding.favorite.setImageResource(R.drawable.ic_favorite)
                    database.getReference("favoriteCount").child(foodId).get()
                        .addOnSuccessListener {
                            if(!it.exists()) {
                                database.getReference("favoriteCount").child(foodId).setValue(1)
                            } else {
                                val qlt = it.value as Long
                                database.getReference("favoriteCount").child(foodId).setValue(qlt + 1)
                            }
                        }
                } else {
                    database.getReference("favorites/$uId").child(foodId).removeValue()
                    binding.favorite.setImageResource(R.drawable.ic_favorite_border)
                    database.getReference("favoriteCount").child(foodId).get()
                        .addOnSuccessListener {
                            val qlt = it.value as Long
                            database.getReference("favoriteCount").child(foodId).setValue(qlt - 1)
                        }
                }
            }
    }

    private fun loadFavorite() {
        database.getReference("favorites/$uId").child(foodId).get()
            .addOnSuccessListener {
                if(!it.exists()) {
                    binding.favorite.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    binding.favorite.setImageResource(R.drawable.ic_favorite)
                }
            }
    }

    private fun sendCmt() {
        val content = binding.cmtContent.text.toString()
        if(content != "") {
            val key = database.getReference("comments").push().key.toString()
            comment = Comment(
                id = key,
                time = System.currentTimeMillis().toString(),
                content = content,
                uId = uId,
                foodId = foodId
            )
            database.getReference("comments").child(key).setValue(comment)
                .addOnSuccessListener {
                    binding.cmtContent.text.clear()
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
        database.reference.child("foods").child(foodId).get()
            .addOnSuccessListener {
                val food = it.getValue(Food::class.java)
                Glide.with(this)
                    .load(food!!.imageUrl)
                    .into(binding.foodImage)

                binding.foodName.text = food.name.capitalize()

                binding.desContent.text = food.des

                Glide.with(this)
                    .load(food.user.avatarUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.userAvatar)

                binding.userDisplayname.text = food.user.displayName

//              Load danh sach nguyen lieu
                val listMaterial: ArrayList<Material> = ArrayList()
                for(materialItem in it.child("materials").children) {
                    val material = materialItem.getValue(Material::class.java)
                    listMaterial.add(material!!)
                }
                loadMaterial(listMaterial)

//              Load danh sach buoc lam
                val listStep: ArrayList<String> = ArrayList()
                for(stepItem in it.child("steps").children) {
                    listStep.add(stepItem!!.value.toString())
                }
                loadStep(listStep)

//              Load danh sach thuc an moi cua nguoi dung
                loadNewFood(food.user.uId)

//              Load thoi gian dang thuc an
                binding.foodTime.text =  getDateTime(it.child("timeUpdate").value.toString())

                if(uId == food.user.uId) {
                    binding.editBtn.visibility = View.VISIBLE
                }

                binding.menuBtn.setOnClickListener {
                    showMenuReport(it, this, food)
                }

                binding.profileButton.setOnClickListener {
                    val intent = Intent(this@DetailActivity, ProfileActivity::class.java)
                    intent.putExtra("uId", food.user.uId)
                    startActivity(intent)
                }

                binding.editBtn.setOnClickListener {
                    val intent = Intent(this@DetailActivity, EditFoodActivity::class.java)
                    intent.putExtra("foodId", foodId)
                    startActivity(intent)
                }

            }
    }

    private fun loadMaterial(listMaterial: ArrayList<Material>) {
        for (i in 0 until listMaterial.size) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_material, null)
            view.findViewById<TextView>(R.id.material_name).text = listMaterial[i].name
            view.findViewById<TextView>(R.id.material_quality).text = listMaterial[i].quality
            binding.materialList.addView(view)
        }
    }

    private fun loadStep(listStep: ArrayList<String>) {
        for (i in 0 until listStep.size) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_step, null)
            view.findViewById<TextView>(R.id.order).text = (i+1).toString()
            view.findViewById<TextView>(R.id.content).text = listStep[i]
            binding.stepList.addView(view)
        }
    }

    private fun loadCmt() {
        val query = database.getReference("comments").orderByChild("foodId").equalTo(foodId)
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

    private fun showMenuReport(view: View, context: Context, food: Food) {
        val popupMenu = PopupMenu(context.applicationContext, view)
            if (uId == food.user.uId) {
                popupMenu.inflate(R.menu.menu_popup_user)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete -> {
                            AlertDialog.Builder(context)
                                .setTitle("Xóa công thức")
                                .setMessage("Bạn có chắc chắn muốn xóa công thức này không?")
                                .setPositiveButton("Xóa") { dialog, _ ->
                                    deleteData(food.id)
                                    dialog.dismiss()
                                }
                                .setNegativeButton("Đóng") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                            true
                        }
                        else -> true
                    }
                }
            } else {
                popupMenu.inflate(R.menu.menu_popup)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.report -> {
                            AlertDialog.Builder(context)
                                .setTitle("Báo cáo món ăn")
                                .setMessage("Bạn đã xác nhận món ăn này không đúng nội dung về công thức nầu ăn chưa?")
                                .setPositiveButton("Báo cáo") { dialog, _ ->
                                    database.getReference("reports").child(foodId).get()
                                        .addOnSuccessListener {
                                            if(it.exists()){
                                                val report = it.getValue(Report::class.java)
                                                report!!.quality = report.quality + 1
                                                database.getReference("reports/$foodId").setValue(report)
                                            } else {
                                                val report = Report(id = foodId, quality = 1)
                                                database.getReference("reports/$foodId").setValue(report)
                                            }
                                            Toast.makeText(context, "Đã báo cáo món ăn", Toast.LENGTH_SHORT).show()
                                        }
                                    dialog.dismiss()
                                }
                                .setNegativeButton("Đóng") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                            true
                        }
                        else -> true
                    }
                }
            }
        popupMenu.show()
    }

    private fun deleteData(id: String) {
        val dltFood = database.getReference("foods").child(id)
        val dltSearch = database.getReference("searchCount").child(id)
        val dltFvrCount = database.getReference("favoriteCount").child(id)
        val dltReport = database.getReference("reports").child(id)
        database.getReference("comments")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cmtSnapshot in snapshot.children) {
                        if(cmtSnapshot.child("foodId").value.toString() == id) {
                            database.getReference("comments").child(cmtSnapshot.key.toString()).removeValue()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        database.getReference("favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(userSnapshot in snapshot.children) {
                        for(foodSnapshot in userSnapshot.children) {
                            if(foodSnapshot.key.toString() == id) {
                                database.getReference("favorites").child(userSnapshot.key.toString()).child(id).removeValue()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        database.getReference("histories")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(userSnapshot in snapshot.children) {
                        for(foodSnapshot in userSnapshot.children) {
                            if(foodSnapshot.key.toString() == id) {
                                database.getReference("histories").child(userSnapshot.key.toString()).child(id).removeValue()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        dltFood.removeValue()
        dltFvrCount.removeValue()
        dltSearch.removeValue()
        dltReport.removeValue()

        Toast.makeText(this@DetailActivity, "Xóa thành công", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@DetailActivity, HomeActivity::class.java))
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

}