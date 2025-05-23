package com.example.bitbloomadmin.Repository

import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.models.PlanModel
import com.example.bitbloomadmin.utils.Status
import kotlinx.coroutines.flow.Flow

class PlanRepository(private val firebaseHelper: FirebaseHelper) {

    // Get all plans as real-time Flow
    fun getAllPlans(): Flow<List<PlanModel>> = firebaseHelper.getAllPlans()

    // Add a new plan
    suspend fun addPlan(planModel: PlanModel): Status = firebaseHelper.addPlan(planModel)

    // Update an existing plan
    suspend fun updatePlan(planModel: PlanModel): Status =
        firebaseHelper.updatePlan(planModel)

    // Delete a plan
    suspend fun deletePlan(planName: String): Status = firebaseHelper.deletePlan(planName)
}
