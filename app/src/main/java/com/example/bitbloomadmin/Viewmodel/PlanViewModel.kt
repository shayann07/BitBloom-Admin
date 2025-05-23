package com.example.bitbloomadmin.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.bitbloomadmin.Repository.PlanRepository
import com.example.bitbloomadmin.models.PlanModel
import kotlinx.coroutines.flow.Flow

class PlanViewModel(private val repository: PlanRepository) : ViewModel() {

    val plansFlow: Flow<List<PlanModel>> = repository.getAllPlans()

    suspend fun addPlan(plan: PlanModel) = repository.addPlan(plan)
    suspend fun updatePlan(plan: PlanModel) = repository.updatePlan(plan)
    suspend fun deletePlan(name: String) = repository.deletePlan(name)
}
