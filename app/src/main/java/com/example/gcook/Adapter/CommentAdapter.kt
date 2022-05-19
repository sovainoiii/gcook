package com.example.gcook.Adapter

import android.annotation.SuppressLint
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
import java.sql.Date
import java.text.SimpleDateFormat

class CommentAdapter(private val listCmt: ArrayList<Comment>)
    :RecyclerView.Adapter<CommentAdapter.CommentViewHolder>(){

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.cmt_avatar)
        val displayName = itemView.findViewById<TextView>(R.id.cmt_name)
        val time = itemView.findViewById<TextView>(R.id.time)
        val content = itemView.findViewById<TextView>(R.id.cmt_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cmt, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val cmt = listCmt[position]
        holder.displayName.text = cmt.user.displayName
        holder.time.text = getDateTime(cmt.time)
        holder.content.text = cmt.content
        Glide.with(holder.itemView.context)
            .load(cmt.user.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.avatar)
    }

    override fun getItemCount(): Int {
        return listCmt.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy")
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}