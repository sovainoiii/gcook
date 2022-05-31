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
import com.google.firebase.database.FirebaseDatabase

class FoodProfileAdapter (private val listFood: ArrayList<Food>)
    : RecyclerView.Adapter<FoodProfileAdapter.FoodProfilerViewHolder>(){

    var onItemClick: ((String)->Unit)? = null

    class FoodProfilerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageFood = itemView.findViewById<ImageView>(R.id.img_food)
        val nameFood = itemView.findViewById<TextView>(R.id.name_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodProfilerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_popular, parent, false)
        return FoodProfilerViewHolder((view))
    }

    override fun onBindViewHolder(holder: FoodProfilerViewHolder, position: Int) {
        val food = listFood[position]
                holder.nameFood.text = food!!.name.capitalize()
                Glide.with(holder.itemView.context)
                    .load(food.imageUrl)
                    .into(holder.imageFood)
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(food.id)
                }
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

}