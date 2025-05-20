package com.example.bitbloomadmin.models

import com.google.firebase.Timestamp

/**
 * Firestore data model for a user plan.
 */
data class UserPlanModel(
    val id: String = "",                 // Firestore document ID
    val user_id: String = "",            // Unique user identifier
    val invested_amount: Double = 0.0,     // Amount invested in the plan
    val PlanStatus: String = "",         // "active", "expired", etc.
    val start_date: Timestamp = Timestamp.now()  // When the plan started
)
