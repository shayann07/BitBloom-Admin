package com.example.bitbloomadmin.Repository


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TopLeadersRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val leadersCol = firestore.collection("top_leaders")

    /**
     * Finds the document with field "rank" == [rank] and updates its
     * "id" and "total_business" fields. Throws if no matching doc.
     */
    suspend fun updateLeader(
        rank: Int,
        userId: String,
        totalBusiness: Double
    ) {
        // 1) query for the doc matching this rank
        val snapshot = leadersCol
            .whereEqualTo("rank", rank)
            .limit(1)
            .get()
            .await()

        if (snapshot.isEmpty) {
            throw IllegalArgumentException("No top-leader found with rank $rank")
        }

        // 2) update only the two fields
        val docRef = snapshot.documents.first().reference
        val updates = mapOf(
            "id" to userId,
            "total_business" to totalBusiness
        )
        docRef.update(updates).await()
    }
}