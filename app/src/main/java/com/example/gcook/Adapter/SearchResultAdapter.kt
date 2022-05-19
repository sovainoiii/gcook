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

class SearchResultAdapter(private val listFood: ArrayList<Food>)
    :RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>(){

    var onItemClick: ((String) -> Unit)? = null

   class SearchResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
       val nameFood = itemView.findViewById<TextView>(R.id.food_name)
       val desFood = itemView.findViewById<TextView>(R.id.food_des)
       val displayName = itemView.findViewById<TextView>(R.id.displayName)
       val imgFood = itemView.findViewById<ImageView>(R.id.food_image)
       val avt = itemView.findViewById<ImageView>(R.id.avatar)
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val food = listFood[position]
        holder.nameFood.text = food.name.capitalize()
        holder.desFood.text = food.des
        holder.displayName.text = food.user.displayName
        Glide.with(holder.itemView.context).load(food.imageUrl).into(holder.imgFood)
        Glide.with(holder.itemView.context).load(food.user.avatarUrl).apply (RequestOptions.circleCropTransform()).into(holder.avt)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(food.id)
        }
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

}