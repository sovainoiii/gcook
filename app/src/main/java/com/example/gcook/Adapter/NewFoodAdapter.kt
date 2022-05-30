package com.example.gcook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gcook.Model.Food
import com.example.gcook.R

class NewFoodAdapter(private val listFood: ArrayList<Food>)
    :RecyclerView.Adapter<NewFoodAdapter.NewFoodViewHolder>(){

    var onItemClick: ((String) -> Unit)? = null

    class NewFoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.food_new_image)
        val name = itemView.findViewById<TextView>(R.id.food_new_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_food, parent, false)
        return NewFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewFoodViewHolder, position: Int) {
        val food: Food = listFood[position]
        Glide.with(holder.itemView.context)
            .load(food.imageUrl)
            .into(holder.image)
        holder.name.text = food.name.capitalize()
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(food.id)
        }
    }

    override fun getItemCount(): Int {
        return  listFood.size
    }

}