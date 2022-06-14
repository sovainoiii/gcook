package com.example.gcook.UI.Admin.Fragments

import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gcook.Adapter.AdminCommentAdapter
import com.example.gcook.Model.Comment
import com.example.gcook.Model.Food
import com.example.gcook.R
import com.example.gcook.databinding.FragmentManageCommentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageCommentFragment : Fragment() {

    private lateinit var binding: FragmentManageCommentBinding
    private lateinit var listCmt: ArrayList<Comment>
    private lateinit var cmtAdapter: AdminCommentAdapter
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageCommentBinding.inflate(inflater, container, false)

        listCmt = ArrayList()
        cmtAdapter = AdminCommentAdapter(listCmt)
        binding.listCmt.layoutManager = LinearLayoutManager(activity)
        binding.listCmt.adapter = cmtAdapter

        loadCmt()
        binding.search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchCmt(p0!!.toLowerCase())
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchCmt(p0!!.toLowerCase())
                return false
            }

        })

        return binding.root
    }

    private fun searchCmt(content: String) {
        val query = database.getReference("comments").orderByChild("time")
        if(!content.trim().isEmpty()) {
            query.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listCmt.clear()
                    for (cmtSnapshot in snapshot.children) {
                        val cmt = cmtSnapshot.getValue(Comment::class.java)
                        if(cmt!!.content.contains(content))
                            listCmt.add(0, cmt)
                    }
                    cmtAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            loadCmt()
        }
    }

    private fun loadCmt() {
        val query = database.getReference("comments").orderByChild("time")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listCmt.clear()
                for (cmtSnapshot in snapshot.children) {
                    val cmt = cmtSnapshot.getValue(Comment::class.java)
                    listCmt.add(0, cmt!!)
                }
                cmtAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}