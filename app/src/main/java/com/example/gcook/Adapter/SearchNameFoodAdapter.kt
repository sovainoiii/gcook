package com.example.gcook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gcook.Model.Food
import com.example.gcook.R

class SearchNameFoodAdapter(private val listFood: ArrayList<Food>)
    :RecyclerView.Adapter<SearchNameFoodAdapter.SearchNameFoodViewHolder>(){

    var onItemClick: ((String)->Unit)? = null

    class SearchNameFoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameFood = itemView.findViewById<TextView>(R.id.name_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchNameFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_name_food, parent, false)
        return SearchNameFoodViewHolder((view))
    }

    override fun onBindViewHolder(holder: SearchNameFoodViewHolder, position: Int) {
        val food = listFood[position]
        holder.nameFood.text = food.name.capitalize()
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(food.id)
        }
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

}