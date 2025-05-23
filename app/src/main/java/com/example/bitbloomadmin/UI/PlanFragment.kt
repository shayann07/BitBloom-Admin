package com.example.bitbloomadmin.UI

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.PlanViewModelFactory
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.PlanRepository
import com.example.bitbloomadmin.Viewmodel.PlanViewModel
import com.example.bitbloomadmin.adapter.PlanAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.google.android.material.button.MaterialButton

class PlanFragment : Fragment(R.layout.fragment_plan) {

    private lateinit var planViewModel: PlanViewModel
    private lateinit var adapter: PlanAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Build your repository + viewModel
        val repo = PlanRepository(FirebaseHelper(requireContext()))
        val factory = PlanViewModelFactory(repo)
        planViewModel = ViewModelProvider(this, factory)
            .get(PlanViewModel::class.java)

        // 2) Setup RecyclerView + Adapter
        adapter = PlanAdapter()
        val rv = view.findViewById<RecyclerView>(R.id.rvPlans)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // 3) Collect the plansFlow and submit to adapter
        viewLifecycleOwner.lifecycleScope.launch {
            planViewModel.plansFlow.collect { list ->
                adapter.submitList(list)
            }
        }

        // 4) Hook up the "Add New Plan" button
        view.findViewById<MaterialButton>(R.id.btnAddPlan)
            .setOnClickListener {
                // e.g. navigate to your PlanDetailFragment
                findNavController().navigate(R.id.action_planFragment_to_addPlanFragment)
            }
    }
}
