package com.example.bitbloomadmin.UI

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.PlanViewModelFactory
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.PlanRepository
import com.example.bitbloomadmin.Viewmodel.PlanViewModel
import com.example.bitbloomadmin.models.PlanModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddPlanFragment : BaseFragment() {

    private lateinit var viewModel: PlanViewModel
    private var isEditMode = false
    private var originalPlanId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // 1) obtain ViewModel
        val repo    = PlanRepository(FirebaseHelper(requireContext()))
        val factory = PlanViewModelFactory(repo)
        viewModel = ViewModelProvider(requireActivity(), factory)
            .get(PlanViewModel::class.java)

        // 2) find all inputs
        val etName   = view.findViewById<TextInputEditText>(R.id.etPlanName)
        val etMin    = view.findViewById<TextInputEditText>(R.id.etMinAmount)
        val etDur    = view.findViewById<TextInputEditText>(R.id.etDurationDays)
        val etDaily  = view.findViewById<TextInputEditText>(R.id.etDailyPercentage)
        val etDirect = view.findViewById<TextInputEditText>(R.id.etDirectProfit)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirmOrUpdate)

        // 3) detect add vs. edit
        arguments?.let { args ->
            isEditMode      = args.getBoolean("isEdit", false)
            if (isEditMode) {
                originalPlanId = args.getString("planId")
                // prefill all fields
                etName.setText(    args.getString("planName", "") )
                etMin.setText(     args.getDouble("minInvestment", 0.0).toString() )
                etDur.setText(     args.getInt   ("durationDays", 0)   .toString() )
                etDaily.setText(   args.getDouble("dailyPercent", 0.0).toString() )
                etDirect.setText(  args.getDouble("directProfit",  0.0).toString() )
                btnConfirm.text = "Update Plan"
            } else {
                btnConfirm.text = "Add Plan"
            }
        }

        // 4) wire the button
        btnConfirm.setOnClickListener {
            val name   = etName.text.toString().trim()
            val min    = etMin.text.toString().toDoubleOrNull() ?: 0.0
            val days   = etDur.text.toString().toIntOrNull() ?: 0
            val daily  = etDaily.text.toString().toDoubleOrNull() ?: 0.0
            val direct = etDirect.text.toString().toDoubleOrNull() ?: 0.0

            val plan = PlanModel(
                name          = name,
                minInvestment = min,
                durationDays  = days,
                percentage    = daily,
                directProfit  = direct
            )

            // 5) call the right repository method
            viewLifecycleOwner.lifecycleScope.launch {
                if (isEditMode && originalPlanId != null) {
                    viewModel.updatePlan(plan)
                } else {
                    viewModel.addPlan(plan)
                }
                findNavController().popBackStack()
            }
        }
    }
}
