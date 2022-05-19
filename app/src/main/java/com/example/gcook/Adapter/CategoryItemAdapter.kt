package com.example.gcook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Model.Food
import com.example.gcook.R

class CategoryItemAdapter(private val listFood: ArrayList<Food>)
    : RecyclerView.Adapter<CategoryItemAdapter.CategoryItemHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    inner class CategoryItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val avt: ImageView = itemView.findViewById(R.id.avatar)
        val userName: TextView = itemView.findViewById(R.id.display_name)
        val nameFood: TextView = itemView.findViewById(R.id.name_food)
        val imgFood: ImageView = itemView.findViewById(R.id.img_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cate_food, parent, false)
        return CategoryItemHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryItemHolder, position: Int) {
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
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(food.id)
        }
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

}