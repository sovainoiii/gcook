package com.example.gcook.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Model.Food
import com.example.gcook.R
import com.example.gcook.UI.Home.HomeActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminFoodAdapter(private val listFood: ArrayList<Food>)
    : RecyclerView.Adapter<AdminFoodAdapter.AdminFoodViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    private val database = FirebaseDatabase.getInstance()

    inner class AdminFoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val avt: ImageView = itemView.findViewById(R.id.avatar)
        val userName: TextView = itemView.findViewById(R.id.display_name)
        val nameFood: TextView = itemView.findViewById(R.id.name_food)
        val imgFood: ImageView = itemView.findViewById(R.id.img_food)
        val food: ConstraintLayout = itemView.findViewById(R.id.food)
        val menu: ImageView = itemView.findViewById(R.id.menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cate_food, parent, false)
        return AdminFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminFoodViewHolder, position: Int) {
        val food = listFood[position]
        holder.userName.text = food.user.displayName
        holder.nameFood.text = food.name.capitalize()
        Glide.with(holder.itemView.context)
            .load(food.user.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.avt)
        Glide.with(holder.itemView.context)
            .load(food.imageUrl)
            .into(holder.imgFood)
        holder.food.setOnClickListener {
            onItemClick?.invoke(food.id)
        }
        holder.menu.setOnClickListener {
            showMenuReport(it, holder.itemView.context, food.id)
        }
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

    private fun showMenuReport(view: View, context: Context, foodId: String) {
        val popupMenu = PopupMenu(context.applicationContext, view)
        popupMenu.inflate(R.menu.menu_popup_user)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    AlertDialog.Builder(context)
                        .setTitle("Xóa công thức")
                        .setMessage("Bạn có chắc chắn muốn xóa công thức này?")
                        .setPositiveButton("Xóa") { dialog, _ ->
                            deleteFood(foodId)
                            Toast.makeText(context, "Đã xóa công thức", Toast.LENGTH_SHORT).show()
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
        popupMenu.show()
    }

    private fun deleteFood(id: String) {
        val dltFood = database.getReference("foods").child(id)
        val dltSearch = database.getReference("searchCount").child(id)
        val dltFvrCount = database.getReference("favoriteCount").child(id)
        val dltReport = database.getReference("reports").child(id)
        database.getReference("comments")
            .addValueEventListener(object : ValueEventListener {
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
            .addValueEventListener(object : ValueEventListener {
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
            .addValueEventListener(object : ValueEventListener {
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
    }

}