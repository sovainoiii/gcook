package com.example.gcook.UI.Home.Fragment.TabLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gcook.Adapter.FoodProfileAdapter
import com.example.gcook.Model.Food
import com.example.gcook.R
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.FragmentFavotiteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavotiteFragment : Fragment() {

    private lateinit var binding: FragmentFavotiteBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var uId: String
    private lateinit var listFood: ArrayList<Food>
    private lateinit var foodAdapter: FoodProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavotiteBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity
        uId = homeActivity.getId()

        listFood = ArrayList()
        foodAdapter = FoodProfileAdapter(listFood)
        binding.listFood.layoutManager = GridLayoutManager(activity, 2)
        binding.listFood.adapter = foodAdapter
        foodAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }

        loadFood(uId)

        return binding.root
    }

    private fun loadFood(uId: String) {
        val query = database.getReference("favorites").child(uId).orderByChild("name")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listFood.clear()
                    for (foodSnapshot in snapshot.children){
                        val food = foodSnapshot.getValue(Food::class.java)
                        listFood.add(food!!)
                    }
                    foodAdapter.notifyDataSetChanged()
                } else {
                    binding.noFood.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}