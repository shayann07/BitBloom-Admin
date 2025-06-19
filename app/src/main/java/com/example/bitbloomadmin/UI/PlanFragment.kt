package com.example.bitbloomadmin.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.adapter.PlanAdapter
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.PlanViewModelFactory
import com.example.bitbloomadmin.Repository.PlanRepository
import com.example.bitbloomadmin.Viewmodel.PlanViewModel
import com.example.bitbloomadmin.models.PlanModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class PlanFragment : BaseFragment() {

    private lateinit var planViewModel: PlanViewModel
    private lateinit var adapter: PlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_plan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // Init ViewModel
        val repo    = PlanRepository(FirebaseHelper(requireContext()))
        val factory = PlanViewModelFactory(repo)
        planViewModel = ViewModelProvider(this, factory)
            .get(PlanViewModel::class.java)

        // RecyclerView + Adapter
        adapter = PlanAdapter(emptyList()) { plan -> showPlanOptions(plan) }
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPlans).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter       = this@PlanFragment.adapter
        }

        // Show loader until first page of plans arrives
        showLoading()
        viewLifecycleOwner.lifecycleScope.launch {
            planViewModel.plansFlow.collect {
                adapter.submitList(it)
                hideLoading()
            }
        }

        // Add Plan button
        view.findViewById<MaterialButton>(R.id.btnAddPlan).setOnClickListener {
            findNavController().navigate(
                R.id.addPlanFragment,
                Bundle().apply { putBoolean("isEdit", false) }
            )
        }
    }


    private fun showPlanOptions(plan: PlanModel) {
        val items = arrayOf("Edit Plan", "Delete Plan")
        AlertDialog.Builder(requireContext())
            .setTitle(plan.name)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        // “Edit” → same fragment, but with isEdit=true + all fields
                        val bundle = Bundle().apply {
                            putBoolean("isEdit", true)
                            putString("planName", plan.name)
                            putDouble("minInvestment", plan.minInvestment)
                            putInt("durationDays", plan.durationDays)
                            putDouble("dailyPercent", plan.percentage)
                            putDouble("directProfit", plan.directProfit)
                            putDouble("bonus", plan.bonusPercentage)
                        }
                        findNavController().navigate(
                            R.id.addPlanFragment,
                            bundle
                        )
                    }
                    1 -> confirmDelete(plan)
                }
            }
            .show()
    }

    private fun confirmDelete(plan: PlanModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Plan")
            .setMessage("Are you sure you want to delete \"${plan.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    planViewModel.deletePlan(plan.name)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}