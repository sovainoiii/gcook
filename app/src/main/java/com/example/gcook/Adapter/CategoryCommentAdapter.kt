package com.example.gcook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.Model.Comment
import com.example.gcook.R

class CategoryCommentAdapter(private val listComment: ArrayList<Comment>)
    :RecyclerView.Adapter<CategoryCommentAdapter.CategoryCommentViewHolder>(){

    var onItemClick: ((String) -> Unit)?? = null

    class CategoryCommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageFood = itemView.findViewById<ImageView>(R.id.image_food)
        val imageUser = itemView.findViewById<ImageView>(R.id.avatar)
        val nameFood = itemView.findViewById<TextView>(R.id.name_food)
        val displayName = itemView.findViewById<TextView>(R.id.display_name)
        val content = itemView.findViewById<TextView>(R.id.content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryCommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cate_cmt, parent, false)
        return CategoryCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryCommentViewHolder, position: Int) {
        val comment = listComment[position]
        Glide.with(holder.itemView.context)
            .load(comment.food.imageUrl)
            .into(holder.imageFood)
        Glide.with(holder.itemView.context)
            .load(comment.user.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.imageUser)
        holder.nameFood.text = comment.food.name.capitalize()
        holder.displayName.text = comment.user.displayName
        holder.content.text = comment.content
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(comment.food.id)
        }
    }

    override fun getItemCount(): Int {
        return listComment.size
    }

}