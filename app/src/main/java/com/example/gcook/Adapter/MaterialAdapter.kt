package com.example.gcook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.gcook.Model.Material
import com.example.gcook.R

class MaterialAdapter(private val listMaterial: ArrayList<Material>)
    : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    class MaterialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val materialName = itemView.findViewById<TextView>(R.id.material_name)
        val materialQuality = itemView.findViewById<TextView>(R.id.material_quality)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = listMaterial[position]
        holder.materialName.text = material.name
        holder.materialQuality.text = material.quality
    }

    override fun getItemCount(): Int {
        return listMaterial.size
    }

}