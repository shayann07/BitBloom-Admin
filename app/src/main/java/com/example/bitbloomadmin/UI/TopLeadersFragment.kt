package com.example.bitbloomadmin.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Viewmodel.TopLeadersVMFactory
import com.example.bitbloomadmin.Viewmodel.TopLeadersViewModel
import com.example.bitbloomadmin.models.TopLeader
import com.example.bitbloomadmin.ui.BaseFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TopLeadersFragment : BaseFragment() {

    private val viewModel: TopLeadersViewModel by viewModels { TopLeadersVMFactory() }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, b: Bundle?) =
        inflater.inflate(R.layout.fragment_top_leaders, c, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        val etUserID = view.findViewById<TextInputEditText>(R.id.etUserID)
        val etRank = view.findViewById<TextInputEditText>(R.id.etRank)
        val etTotalBusiness = view.findViewById<TextInputEditText>(R.id.etTotalBusiness)
        val btnUpdate = view.findViewById<MaterialButton>(R.id.btnUpdate)
        val rv = view.findViewById<RecyclerView>(R.id.rvLeaders)

        // 1) RecyclerView setup
        rv.layoutManager = LinearLayoutManager(requireContext())
        val query: Query = FirebaseFirestore
            .getInstance()
            .collection("top_leaders")
            .orderBy("rank", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<TopLeader>()
            .setQuery(query, TopLeader::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        val adapter = object : FirestoreRecyclerAdapter<TopLeader, LeaderVH>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderVH {
                val v = layoutInflater.inflate(R.layout.item_top_leader, parent, false)
                return LeaderVH(v)
            }

            override fun onBindViewHolder(holder: LeaderVH, position: Int, model: TopLeader) {
                holder.bind(model)
            }
        }
        rv.adapter = adapter
        hideLoading()

        // 2) “Update” button logic (unchanged)
        btnUpdate.setOnClickListener {
            val rank = etRank.text.toString().toIntOrNull()
            if (rank == null || rank !in 1..10) {
                Snackbar.make(view, "Rank must be between 1 and 10", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val uid = etUserID.text.toString().trim()
            if (uid.isEmpty()) {
                Snackbar.make(view, "User ID cannot be empty", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val biz = etTotalBusiness.text.toString().toDoubleOrNull()
            if (biz == null) {
                Snackbar.make(view, "Enter valid total business", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.updateLeader(rank, uid, biz)
        }

        // 3) Observe ViewModel for loading / success / error
        viewModel.isLoading.observe(viewLifecycleOwner) { if (it) showLoading() else hideLoading() }
        viewModel.success.observe(viewLifecycleOwner) {
            it?.let { Snackbar.make(view, "Updated!", Snackbar.LENGTH_SHORT).show() }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            it?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show() }
        }
    }

    private class LeaderVH(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val tvRank = itemView.findViewById<TextView>(R.id.tvRank)
        private val tvId = itemView.findViewById<TextView>(R.id.tvUserId)
        private val tvBiz = itemView.findViewById<TextView>(R.id.tvTotalBiz)
        fun bind(m: TopLeader) {
            tvRank.text = "#${m.rank}"
            tvId.text = m.id
            tvBiz.text = m.total_business.toString()
        }
    }
}