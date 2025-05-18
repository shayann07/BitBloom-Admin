package com.example.bitbloomadmin.Data.remote

import android.content.Context
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.UserModel

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseHelper(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchUsers(): List<UserModel> {
        return try {
            val snapshot = firestore.collection("users").get().await()
            snapshot.documents.mapNotNull { it.toObject(UserModel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchAccounts(): List<AccountModel> {
        return try {
            val snapshot = firestore.collection("accounts").get().await()
            snapshot.documents.mapNotNull { it.toObject(AccountModel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}