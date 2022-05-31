package com.example.gcook.UI.Home.Fragment.TabLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gcook.Adapter.FoodProfileAdapter
import com.example.gcook.Model.Food
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.FragmentMyFoodBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyFoodFragment : Fragment() {

    private lateinit var binding: FragmentMyFoodBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var uId: String
    private lateinit var listFood: ArrayList<Food>
    private lateinit var foodAdapter: FoodProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyFoodBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity
        uId = homeActivity.getId()

        listFood = ArrayList()
        foodAdapter = FoodProfileAdapter(listFood)
        foodAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }
        binding.listFood.layoutManager = GridLayoutManager(activity, 2)
        binding.listFood.adapter = foodAdapter

        loadFood(uId)

        return binding.root
    }

    private fun loadFood(uId: String) {
        val query = database.getReference("foods").orderByChild("name")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listFood.clear()
                    for (foodSnapshot in snapshot.children){
                        val food = foodSnapshot.getValue(Food::class.java)
                        if(food!!.user.uId == uId){
                            listFood.add(food)
                        }
                    }
                    if(listFood.size == 0) {
                        binding.noFood.visibility = View.VISIBLE
                    }
                    foodAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}