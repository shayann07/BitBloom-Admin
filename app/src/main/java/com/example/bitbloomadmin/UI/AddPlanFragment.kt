package com.example.bitbloomadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.PlanViewModelFactory
import com.example.bitbloomadmin.Repository.PlanRepository
import com.example.bitbloomadmin.Viewmodel.PlanViewModel
import com.example.bitbloomadmin.models.PlanModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddPlanFragment : BaseFragment() {

    private lateinit var viewModel: PlanViewModel
    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_plan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // Obtain ViewModel
        val repo    = PlanRepository(FirebaseHelper(requireContext()))
        val factory = PlanViewModelFactory(repo)
        viewModel   = ViewModelProvider(requireActivity(), factory)[PlanViewModel::class.java]

        // Inputs
        val etName     = view.findViewById<TextInputEditText>(R.id.etPlanName)
        val etMin      = view.findViewById<TextInputEditText>(R.id.etMinAmount)
        val etDur      = view.findViewById<TextInputEditText>(R.id.etDurationDays)
        val etDaily    = view.findViewById<TextInputEditText>(R.id.etDailyPercentage)
        val etDirect   = view.findViewById<TextInputEditText>(R.id.etDirectProfit)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirmOrUpdate)

        // Detect edit mode
        arguments?.let { args ->
            isEditMode = args.getBoolean("isEdit", false)
            if (isEditMode) {
                etName.setText(args.getString("planName", ""))
                etMin.setText(args.getDouble("minInvestment", 0.0).toString())
                etDur.setText(args.getInt("durationDays", 0).toString())
                etDaily.setText(args.getDouble("dailyPercent", 0.0).toString())
                etDirect.setText(args.getDouble("directProfit", 0.0).toString())
                btnConfirm.text = "Update Plan"
            }
        }

        btnConfirm.setOnClickListener {
            val plan = PlanModel(
                name          = etName.text.toString().trim(),
                minInvestment = etMin.text.toString().toDoubleOrNull() ?: 0.0,
                durationDays  = etDur.text.toString().toIntOrNull() ?: 0,
                percentage    = etDaily.text.toString().toDoubleOrNull() ?: 0.0,
                directProfit  = etDirect.text.toString().toDoubleOrNull() ?: 0.0
            )

            /*// Show loader around network call
            showLoading()*/
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    if (isEditMode) viewModel.updatePlan(plan)
                    else             viewModel.addPlan(plan)
                    findNavController().popBackStack()
                } finally {
                    hideLoading()
                }
            }
        }
    }
}
