package com.example.gcook.UI.Home.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gcook.Adapter.CategoryCommentAdapter
import com.example.gcook.Adapter.CategoryItemAdapter
import com.example.gcook.Adapter.FavoriteAdapter
import com.example.gcook.UI.Detail.DetailActivity
import com.example.gcook.Model.Comment
import com.example.gcook.Model.Food
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var listCate: ArrayList<Food> = ArrayList()
    private var listCmt: ArrayList<Comment> = ArrayList()
    private var listFavorite: ArrayList<String> = ArrayList()
    private lateinit var cateAdapter: CategoryItemAdapter
    private lateinit var cmtAdapter: CategoryCommentAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val homeActivity = activity as HomeActivity

        binding.search.setOnClickListener {
            homeActivity.replaceFragment(SearchFoodFragment())
        }

        cateAdapter = CategoryItemAdapter(listCate)
        binding.listNew.layoutManager = GridLayoutManager(activity, 2)
        binding.listNew.adapter = cateAdapter
        cateAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }
        cmtAdapter = CategoryCommentAdapter(listCmt)
        binding.listCmt.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.listCmt.adapter = cmtAdapter
        cmtAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }
        favoriteAdapter = FavoriteAdapter(listFavorite)
        binding.listFavorite.layoutManager = GridLayoutManager(activity, 2)
        binding.listFavorite.adapter = favoriteAdapter
        favoriteAdapter.onItemClick = {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("food_id",it)
            startActivity(intent)
        }

        loadRecycleView()

        return binding.root
    }

    private fun loadRecycleView() {
        val queryCateNew = database.getReference("foods").orderByChild("timeUpdate").limitToLast(10)
        queryCateNew.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    listCate.clear()
                    for (foodSnapshot in snapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)
                        listCate.add(0,food!!)
                    }
                    cateAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val queryCateCmt = database.getReference("comments").orderByChild("time").limitToLast(6)
        queryCateCmt.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    listCmt.clear()
                    for (cmtSnapshot in snapshot.children) {
                        val cmt = cmtSnapshot.getValue(Comment::class.java)
                        listCmt.add(0,cmt!!)
                    }
                    cmtAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val queryFavorite = database.getReference("favoriteCount").orderByValue().limitToLast(10)
        queryFavorite.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (foodSnapshot in snapshot.children) {
                        listFavorite.add(0, foodSnapshot.key.toString())
                    }
                    favoriteAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}
