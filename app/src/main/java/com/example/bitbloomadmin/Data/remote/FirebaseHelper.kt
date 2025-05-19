package com.example.bitbloomadmin.Data.remote

import android.content.Context
import android.util.Log
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.AnnouncementModel
import com.example.bitbloomadmin.models.EarningsModel
import com.example.bitbloomadmin.models.InvestmentModel
import com.example.bitbloomadmin.models.PlanModel
import com.example.bitbloomadmin.models.UserModel
import com.example.bitbloomadmin.models.WithdrawModel
import com.example.bitbloomadmin.models.WithdrawWithUserName
import com.example.bitbloomadmin.utils.Status
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseHelper(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val plansCollection = firestore.collection("plans")
    private val usersCollection = firestore.collection("users")
    private val accountsCollection = firestore.collection("accounts")

    suspend fun fetchUsers(): List<UserModel> {
        return try {
            val snapshot = firestore.collection("users").get().await()
            println("📥 [FirebaseHelper] Fetched ${snapshot.size()} users from Firestore")

            val users = snapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(UserModel::class.java)
                user?.copy(id = doc.getString("id") ?: doc.getString("uid") ?: doc.id)
            }
            users
        } catch (e: Exception) {
            println("❌ [FirebaseHelper] Failed to fetch users: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchAccounts(): List<AccountModel> {
        return try {
            val snapshot = accountsCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val earnings = doc.get("earnings") as? Map<String, Number> ?: emptyMap()
                val investment = doc.get("investment") as? Map<String, Number> ?: emptyMap()

                AccountModel(
                    accountId = doc.id,
                    userId = doc.getString("user_id") ?: "",
                    createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now(),
                    status = doc.getString("status") ?: "",
                    investment = InvestmentModel(
                        total_deposit = investment["total_deposit"]?.toDouble() ?: 0.0,
                        stake_profit = investment["stake_profit"]?.toDouble() ?: 0.0,
                        remaining_balance = investment["remaining_balance"]?.toDouble() ?: 0.0
                    ),
                    earnings = EarningsModel(
                        buying_profit = earnings["buying_profit"]?.toDouble() ?: 0.0,
                        current_balance = earnings["current_balance"]?.toDouble() ?: 0.0,
                        daily_profit = earnings["daily_profit"]?.toDouble() ?: 0.0,
                        lifetime_referral_income = earnings["lifetime_referral_income"]?.toDouble() ?: 0.0,
                        lifetime_roi_income = earnings["lifetime_roi_income"]?.toDouble() ?: 0.0,
                        lifetime_team_income = earnings["lifetime_team_income"]?.toDouble() ?: 0.0,
                        referral_profit = earnings["referral_profit"]?.toDouble() ?: 0.0,
                        team_profit = earnings["team_profit"]?.toDouble() ?: 0.0,
                        total_earned = earnings["total_earned"]?.toDouble() ?: 0.0,
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Error fetching accounts", e)
            emptyList()
        }
    }

    suspend fun fetchWithdrawRequestsWithUserNames(): List<WithdrawWithUserName> {
        return try {
            val users = fetchUsers()
            val userMap = users.associateBy { it.id }

            val snapshot = firestore.collection("withdraw_requests").get().await()

            snapshot.documents.mapNotNull { doc ->
                val userId = doc.getString("userId") ?: return@mapNotNull null
                val userName = userMap[userId]?.name ?: "Unknown"

                val withdrawModel = WithdrawModel(
                    address = doc.getString("address") ?: "",
                    amount = doc.getDouble("amount") ?: 0.0,
                    balanceUpdated = doc.getBoolean("balanceUpdated") ?: false,
                    status = doc.getString("status") ?: "",
                    timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now(),
                    transactionId = doc.getString("transactionId") ?: doc.id,
                    type = doc.getString("type") ?: "withdraw",
                    userId = userId
                )

                WithdrawWithUserName(
                    withdraw = withdrawModel,
                    userName = userName
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Error fetching withdraw transactions", e)
            emptyList()
        }
    }
    fun addAnnouncement(announcement: AnnouncementModel) {
        firestore.collection("announcements")
            .add(announcement)
            .addOnSuccessListener {
                val documentId = it.id
                firestore.collection("announcements").document(documentId).update("id",documentId)
            }
            .addOnFailureListener {
                it.localizedMessage
            }
    }

    suspend fun addPlan(planModel: PlanModel): Status {
        return try {
            // Check if a plan with the same name exists
            val plansSnapshot = firestore.collection("plans").get().await()
            val isPlanNameExists = plansSnapshot.documents.any {
                it.getString("name") == planModel.name
            }
            if (isPlanNameExists) return Status.PLAN_EXISTS

            // Set updatedAt to server timestamp
            val planData = planModel.copy(updatedAt = FieldValue.serverTimestamp()).toMap()

            // Add the plan
            firestore.collection("plans").add(planData).await()

            Log.d("FirebaseHelper", "✅ Plan added successfully!")
            Status.SUCCESS
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "❌ Error adding plan: ${e.message}")
            Status.ERROR
        }
    }
    suspend fun updatePlan(planModel: PlanModel, planDocId: String): Status {
        return try {
            // Check for duplicate plan name, excluding this planDocId
            val plansSnapshot = firestore.collection("plans")
                .whereEqualTo("name", planModel.name)
                .get()
                .await()

            // If more than 1 doc with the same name (including this one), it's a duplicate
            val isDuplicateName = plansSnapshot.documents.count { it.id != planDocId } > 0
            Log.d("FirebaseHelper", "isDuplicateName: $isDuplicateName")

            if (isDuplicateName) return Status.PLAN_EXISTS

            // Update existing plan, set updatedAt to server timestamp
            val planData = planModel.copy(updatedAt = FieldValue.serverTimestamp()).toMap()

            firestore.collection("plans")
                .document(planDocId)
                .set(planData, SetOptions.merge())
                .await()

            Log.d("FirebaseHelper", "✅ Plan updated successfully!")
            Status.SUCCESS
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "❌ Error updating plan: $e")
            Status.ERROR
        }
    }
    fun getAllPlans(): Flow<List<PlanModel>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = FirebaseFirestore.getInstance()
            .collection("plans")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val plans = snapshot?.documents?.mapNotNull { it.toObject(PlanModel::class.java) } ?: emptyList()
                trySend(plans).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }
    suspend fun deletePlan(planDocId: String): Status {
        return try {
            firestore.collection("plans")
                .document(planDocId)
                .delete()
                .await()
            Log.d("FirebaseHelper", "✅ Plan deleted successfully!")
            Status.SUCCESS
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "❌ Error deleting plan: ${e.message}")
            Status.ERROR
        }
    }

}
