package com.example.gcook.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.example.gcook.Model.Food
import com.example.gcook.Model.Report
import com.example.gcook.Model.User
import com.example.gcook.R
import com.example.gcook.UI.Detail.DetailActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminReportAdapter(private val context: Context, private val listReport: ArrayList<Report>)
    : RecyclerView.Adapter<AdminReportAdapter.AdminReportViewHolder>() {
    private val database = FirebaseDatabase.getInstance()

    inner class AdminReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_food)
        val name: TextView = itemView.findViewById(R.id.name_food)
        val quality: TextView = itemView.findViewById(R.id.quality)
        val menu: ImageView = itemView.findViewById(R.id.menu)
        val food: ConstraintLayout = itemView.findViewById(R.id.food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return AdminReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminReportViewHolder, position: Int) {
        val rp = listReport[position]
        database.getReference("foods/${rp.id}").get()
            .addOnSuccessListener {
                val food = it.getValue(Food::class.java)
                Glide.with(holder.itemView.context).load(food!!.imageUrl).into(holder.image)
                holder.name.text = food.name
            }
        holder.quality.text = "S??? l?????ng b??o c??o: "+rp.quality.toString()
        holder.menu.setOnClickListener {
            showMenuReport(it, holder.itemView.context, rp.id)
        }
        holder.food.setOnClickListener{
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("food_id", rp.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listReport.size
    }

    private fun showMenuReport(view: View, context: Context, id: String) {
        val popupMenu = PopupMenu(context.applicationContext, view)
        popupMenu.inflate(R.menu.menu_report)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    AlertDialog.Builder(context)
                        .setTitle("X??a c??ng th???c")
                        .setMessage("B???n c?? ch???c ch???n mu???n x??a c??ng th???c n??y?")
                        .setPositiveButton("X??a") { dialog, _ ->
                            deleteFood(id)
                            Toast.makeText(context, "???? x??a b??nh lu???n", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setNegativeButton("????ng") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                    true
                }
                R.id.delete_report -> {
                    AlertDialog.Builder(context)
                        .setTitle("X??a b??o c??o")
                        .setMessage("B???n c?? ch???c ch???n mu???n x??a b??o c??o n??y?")
                        .setPositiveButton("X??a") { dialog, _ ->
                            database.getReference("reports/$id").removeValue()
                            Toast.makeText(context, "???? x??a b??o c??o", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setNegativeButton("????ng") { dialog, _ ->
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

    private fun deleteFood(id: String) {
        val dltFood = database.getReference("foods").child(id)
        val dltSearch = database.getReference("searchCount").child(id)
        val dltFvrCount = database.getReference("favoriteCount").child(id)
        val dltReport = database.getReference("reports").child(id)
        database.getReference("comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cmtSnapshot in snapshot.children) {
                        if(cmtSnapshot.child("foodId").value.toString() == id) {
                            database.getReference("comments").child(cmtSnapshot.key.toString()).removeValue()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        database.getReference("favorites")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(userSnapshot in snapshot.children) {
                        for(foodSnapshot in userSnapshot.children) {
                            if(foodSnapshot.key.toString() == id) {
                                database.getReference("favorites").child(userSnapshot.key.toString()).child(id).removeValue()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        database.getReference("histories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(userSnapshot in snapshot.children) {
                        for(foodSnapshot in userSnapshot.children) {
                            if(foodSnapshot.key.toString() == id) {
                                database.getReference("histories").child(userSnapshot.key.toString()).child(id).removeValue()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        dltFood.removeValue()
        dltFvrCount.removeValue()
        dltSearch.removeValue()
        dltReport.removeValue()
    }

}