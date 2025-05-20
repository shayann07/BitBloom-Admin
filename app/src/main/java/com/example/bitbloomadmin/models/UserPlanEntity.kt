package com.example.bitbloomadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Room entity for userPlans collection.
 */
@Entity(tableName = "user_plan_table")
data class UserPlanEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val investedAmount: Double,
    val planStatus: String,
    val startDateMillis: Long
) {
    /**
     * Convert this entity to a Firestore map for writing/updating.
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "user_id" to userId,
            "invested_amount" to investedAmount,
            "PlanStatus" to planStatus,
            "start_date" to Timestamp(Date(startDateMillis))
        )
    }

    companion object {
        /**
         * Create a Room entity from a Firestore model.
         */
        fun fromModel(model: UserPlanModel): UserPlanEntity = UserPlanEntity(
            id = model.id,
            userId = model.user_id,
            investedAmount = model.invested_amount,
            planStatus = model.PlanStatus,
            startDateMillis = model.start_date.toDate().time
        )
    }
}
