package com.example.gcook.Adapter

import android.app.AlertDialog
import android.content.Context
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
import com.google.firebase.database.FirebaseDatabase

class FavoriteAdapter (private val listFood: ArrayList<String>)
    : RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    private val database = FirebaseDatabase.getInstance()

    inner class FavoriteHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val avt: ImageView = itemView.findViewById(R.id.avatar)
        val userName: TextView = itemView.findViewById(R.id.display_name)
        val nameFood: TextView = itemView.findViewById(R.id.name_food)
        val imgFood: ImageView = itemView.findViewById(R.id.img_food)
        val food: ConstraintLayout = itemView.findViewById(R.id.food)
        val menu: ImageView = itemView.findViewById(R.id.menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cate_food, parent, false)
        return FavoriteHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteHolder, position: Int) {
        val foodId = listFood[position]
        database.getReference("foods").child(foodId).get()
            .addOnSuccessListener {
                val food = it.getValue(Food::class.java)
                holder.userName.text = food!!.user.displayName
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
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

    private fun showMenuReport(view: View, context: Context, foodId: String) {
        val popupMenu = PopupMenu(context.applicationContext, view)
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
                                        val quality = it.value as Long
                                        database.getReference("reports/$foodId").setValue(quality + 1)
                                    } else {
                                        database.getReference("reports/$foodId").setValue(1)
                                    }
                                    Toast.makeText(context, "Đã báo cáo món ăn", Toast.LENGTH_SHORT).show()
                                }
                            Toast.makeText(context, "Đã báo cáo món ăn", Toast.LENGTH_SHORT).show()
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

}