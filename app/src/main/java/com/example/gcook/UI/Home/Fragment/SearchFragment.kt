package com.example.gcook.UI.Home.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gcook.Adapter.SearchHistoryAdapter
import com.example.gcook.Adapter.SearchNameFoodAdapter
import com.example.gcook.Adapter.SearchPopularAdapter
import com.example.gcook.Model.Food
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var listHistories: ArrayList<Food>
    private lateinit var historiesAdapter: SearchHistoryAdapter
    private lateinit var listPopular: ArrayList<String>
    private lateinit var popularAdapter: SearchPopularAdapter
    private lateinit var uId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity
        uId = homeActivity.getId()

        binding.search.setOnClickListener {
            homeActivity.replaceFragment(SearchFoodFragment())
        }

        listHistories = ArrayList()
        historiesAdapter = SearchHistoryAdapter(listHistories)
        historiesAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }
        binding.listSearch.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, isInLayout)
        binding.listSearch.adapter = historiesAdapter

        listPopular = ArrayList()
        popularAdapter = SearchPopularAdapter(listPopular)
        popularAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }
        binding.listPopular.layoutManager = GridLayoutManager(activity, 2)
        binding.listPopular.adapter = popularAdapter

        loadSearchHistory()
        loadSearchPopular()

        return binding.root
    }

    private fun loadSearchHistory() {
        val query =  database.getReference("histories/$uId").limitToFirst(5)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    listHistories.clear()
                    for(historySnapshot in snapshot.children) {
                        val history = historySnapshot.getValue(Food::class.java)
                        listHistories.add(history!!)
                    }
                    historiesAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadSearchPopular() {
        val query =  database.getReference("searchCount").orderByValue().limitToFirst(10)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    listPopular.clear()
                    for(popularSnapshot in snapshot.children) {
                        println(popularSnapshot.key.toString())
                        listPopular.add(0,popularSnapshot.key.toString())
                    }
                    popularAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}