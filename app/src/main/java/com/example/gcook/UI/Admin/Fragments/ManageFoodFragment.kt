package com.example.gcook.UI.Admin.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gcook.Adapter.AdminFoodAdapter
import com.example.gcook.Adapter.CategoryItemAdapter
import com.example.gcook.Model.Food
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.databinding.FragmentManageFoodBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageFoodFragment : Fragment() {

    private lateinit var binding: FragmentManageFoodBinding
    private lateinit var listFood: ArrayList<Food>
    private lateinit var foodAdapter: AdminFoodAdapter
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageFoodBinding.inflate(inflater, container, false)

        listFood = ArrayList()
        foodAdapter = AdminFoodAdapter(listFood)
        foodAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id", it)
            startActivity(intent)
        }
        binding.listFood.layoutManager = GridLayoutManager(activity, 2)
        binding.listFood.adapter = foodAdapter

        binding.search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchFood(p0!!.toLowerCase())
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchFood(p0!!.toLowerCase())
                return false
            }

        })

        loadListFood()

        return binding.root
    }

    private fun searchFood(name: String) {
        val query = database.getReference("foods").orderByChild("name")
        if(!name.trim().isEmpty()) {
            query.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listFood.clear()
                    for (foodSnapshot in snapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)
                        if(food!!.name.contains(name))
                            listFood.add(food)
                    }
                    foodAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            loadListFood()
        }
    }

    private fun loadListFood() {
        val query = database.getReference("foods").orderByChild("name")
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listFood.clear()
                for (foodSnapshot in snapshot.children) {
                    val food = foodSnapshot.getValue(Food::class.java)
                    listFood.add(food!!)
                }
                foodAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}