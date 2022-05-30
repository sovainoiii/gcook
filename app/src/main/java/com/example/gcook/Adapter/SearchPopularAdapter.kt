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

class SearchPopularAdapter (private val listPopular: ArrayList<String>)
    : RecyclerView.Adapter<SearchPopularAdapter.SearchPopularViewHolder>(){

    var onItemClick: ((String)->Unit)? = null
    private val database = FirebaseDatabase.getInstance()

    class SearchPopularViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageFood = itemView.findViewById<ImageView>(R.id.img_food)
        val nameFood = itemView.findViewById<TextView>(R.id.name_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPopularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_popular, parent, false)
        return SearchPopularViewHolder((view))
    }

    override fun onBindViewHolder(holder: SearchPopularViewHolder, position: Int) {
        val foodId = listPopular[position]
        database.getReference("foods").child(foodId).get()
            .addOnSuccessListener {
                val food = it.getValue(Food::class.java)
                holder.nameFood.text = food!!.name.capitalize()
                Glide.with(holder.itemView.context)
                    .load(food.imageUrl)
                    .into(holder.imageFood)
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(food.id)
                }
            }

    }

    override fun getItemCount(): Int {
        return listPopular.size
    }

}