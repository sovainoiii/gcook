package com.example.gcook.UI.Admin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gcook.Adapter.AdminReportAdapter
import com.example.gcook.Model.Report
import com.example.gcook.R
import com.example.gcook.databinding.FragmentManageReportBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageReportFragment : Fragment() {

    private lateinit var binding: FragmentManageReportBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var listReport: ArrayList<Report>
    private lateinit var reportAdapter: AdminReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageReportBinding.inflate(inflater, container, false)

        listReport = ArrayList()
        reportAdapter = AdminReportAdapter(requireActivity(), listReport)
        binding.listReport.layoutManager = GridLayoutManager(activity, 2)
        binding.listReport.adapter = reportAdapter

        loadReport()

        return binding.root
    }

    private fun loadReport() {
        val query = database.getReference("reports").orderByChild("quality")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listReport.clear()
                for(reportSnapshot in snapshot.children){
                    val report = reportSnapshot.getValue(Report::class.java)
                    listReport.add(0, report!!)
                }
                reportAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}