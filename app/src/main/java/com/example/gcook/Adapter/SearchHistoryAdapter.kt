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

class SearchHistoryAdapter(private val listHistories: ArrayList<Food>)
    : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>(){

    var onItemClick: ((String)->Unit)? = null

    class SearchHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageFood = itemView.findViewById<ImageView>(R.id.img_food)
        val nameFood = itemView.findViewById<TextView>(R.id.name_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return SearchHistoryViewHolder((view))
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val food = listHistories[position]
        holder.nameFood.text = food.name.capitalize()
        Glide.with(holder.itemView.context)
            .load(food.imageUrl)
            .into(holder.imageFood)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(food.id)
        }
    }

    override fun getItemCount(): Int {
        return listHistories.size
    }

}