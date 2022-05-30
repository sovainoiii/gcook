package com.example.gcook.UI.Home.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gcook.Adapter.SearchNameFoodAdapter
import com.example.gcook.Adapter.SearchResultAdapter
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.Model.Food
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.FragmentSearchFoodBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFoodFragment : Fragment() {

    private lateinit var binding: FragmentSearchFoodBinding
    private lateinit var listNameFood: ArrayList<Food>
    private lateinit var nameFoodAdapter: SearchNameFoodAdapter
    private lateinit var listFood: ArrayList<Food>
    private lateinit var foodAdapter: SearchResultAdapter
    private lateinit var uId: String
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchFoodBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity
        uId = homeActivity.getId()

        listNameFood = ArrayList()
        binding.listName.layoutManager = LinearLayoutManager(activity)
        nameFoodAdapter = SearchNameFoodAdapter(listNameFood)
        binding.listName.adapter = nameFoodAdapter
        nameFoodAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            saveHistory(uId, it)
            saveSearchCount(it)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }

        listFood = ArrayList()
        binding.listFood.layoutManager = LinearLayoutManager(activity)
        foodAdapter = SearchResultAdapter(listFood)
        binding.listFood.adapter = foodAdapter
        foodAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            saveHistory(uId, it)
            saveSearchCount(it)
            startActivity(intent)
        }

        binding.back.setOnClickListener {
            homeActivity.replaceFragment(SearchFragment())
        }

        binding.search.setOnQueryTextListener ( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchFoodResult(p0!!.toLowerCase())
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchFood(p0!!.toLowerCase())
                return false
            }

        })

        return binding.root
    }

    private fun saveSearchCount(foodId: String) {
        database.getReference("searchCount").child(foodId).get()
            .addOnSuccessListener {
                if(it.exists()){
                    val quality = it.value as Long
                    database.getReference("searchCount/$foodId").setValue(quality + 1)
                } else {
                    database.getReference("searchCount/$foodId").setValue(1)
                }
            }
    }

    private fun saveHistory(uId: String, foodId: String) {
        database.getReference("histories/$uId").child(foodId).get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    database.getReference("foods").child(foodId).get()
                        .addOnSuccessListener {
                            val food = it.getValue(Food::class.java)
                            database.getReference("histories/$uId").child(foodId).setValue(food)
                        }
                }
            }
    }

    private fun searchFoodResult(name: String) {
        binding.listName.visibility = View.GONE
        binding.searchResultBox.visibility = View.VISIBLE
        val query = database.getReference("foods")
            .orderByChild("name")
        if(name != "") {
            query.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listFood.clear()
                    for (foodSnapshot in snapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)
                        if(food!!.name.contains(name)) {
                            listFood.add(food)
                        }
                    }
                    foodAdapter.notifyDataSetChanged()
                    if(listFood.size == 0) {
                        binding.searchResultBox.visibility = View.GONE
                    } else {
                        binding.result.text = "Kết quả của: '$name'"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun searchFood(name: String) {
        binding.searchResultBox.visibility = View.GONE
        binding.listName.visibility = View.VISIBLE
        val query = database.getReference("foods")
            .orderByChild("name")
        if(name != "") {
            query.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listNameFood.clear()
                    for (foodSnapshot in snapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)
                        if(food!!.name.contains(name)) {
                            listNameFood.add(food)
                        }
                    }
                    nameFoodAdapter.notifyDataSetChanged()
                    if(listNameFood.size == 0) {
                        binding.resultSearch.visibility = View.VISIBLE
                    } else {
                        binding.resultSearch.visibility = View.GONE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            binding.resultSearch.visibility = View.GONE
            listNameFood.clear()
            nameFoodAdapter.notifyDataSetChanged()
        }
    }

}
