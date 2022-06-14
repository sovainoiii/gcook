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
import com.example.gcook.Model.Comment
import com.example.gcook.Model.User
import com.example.gcook.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminCommentAdapter(private val listCmt: ArrayList<Comment>)
    : RecyclerView.Adapter<AdminCommentAdapter.AdminCommentViewHolder>() {
    private val database = FirebaseDatabase.getInstance()

    inner class AdminCommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val avt: ImageView = itemView.findViewById(R.id.avatar)
        val userName: TextView = itemView.findViewById(R.id.username)
        val content: TextView = itemView.findViewById(R.id.content)
        val menu: ImageView = itemView.findViewById(R.id.menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment_manage, parent, false)
        return AdminCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCommentViewHolder, position: Int) {
        val cmt = listCmt[position]
        database.getReference("users/${cmt.uId}").get()
            .addOnSuccessListener {
                val user = it.getValue(User::class.java)
                Glide.with(holder.itemView.context).load(user!!.avatarUrl).apply(RequestOptions.circleCropTransform()).into(holder.avt)
                holder.userName.text = user.displayName
            }
        holder.content.text = cmt.content
        holder.menu.setOnClickListener {
            showMenuReport(it, holder.itemView.context, cmt.id)
        }
    }

    override fun getItemCount(): Int {
        return listCmt.size
    }

    private fun showMenuReport(view: View, context: Context, cmtId: String) {
        val popupMenu = PopupMenu(context.applicationContext, view)
        popupMenu.inflate(R.menu.menu_popup_user)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    AlertDialog.Builder(context)
                        .setTitle("Xóa bình luận")
                        .setMessage("Bạn có chắc chắn muốn xóa bình luận này?")
                        .setPositiveButton("Xóa") { dialog, _ ->
                            deleteCmt(cmtId)
                            Toast.makeText(context, "Đã xóa bình luận", Toast.LENGTH_SHORT).show()
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

    private fun deleteCmt(cmtId: String) {
        database.getReference("comments/$cmtId").removeValue()
    }

}