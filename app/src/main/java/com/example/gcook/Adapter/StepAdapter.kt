package com.example.gcook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gcook.R

class StepAdapter(private val listStep: ArrayList<String>)
    :RecyclerView.Adapter<StepAdapter.StepViewHolder>(){

        class StepViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val order = itemView.findViewById<TextView>(R.id.order)
            val content = itemView.findViewById<TextView>(R.id.content)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = listStep[position]
        holder.order.text = (position + 1).toString()
        holder.content.text = step
    }

    override fun getItemCount(): Int {
        return listStep.size
    }


}