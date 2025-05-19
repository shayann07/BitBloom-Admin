package com.example.bitbloomadmin.Repository

import com.example.bitbloomadmin.Dao.WithdrawDao
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.models.WithdrawModel
import com.example.bitbloomadmin.models.WithdrawWithUserName
import kotlinx.coroutines.flow.Flow

class WithdrawRepository(
    private val withdrawDao: WithdrawDao,
    val firebaseHelper: FirebaseHelper,
) {
    val allWithdrawRequestsFlow: Flow<List<WithdrawModel>> = withdrawDao.getAllWithdrawRequestsFlow()

    // ✅ NEW: With usernames
    val withdrawsWithNamesFlow: Flow<List<WithdrawWithUserName>> = withdrawDao.getAllWithdrawsWithUserName()

    suspend fun refreshWithdrawals() {
        val list = firebaseHelper.fetchWithdrawRequestsWithUserNames().map { it.withdraw }
        withdrawDao.deleteAll()
        withdrawDao.insertAll(list)
    }

    // ✅ NEW: Sync and store WithdrawWithUserName
    suspend fun refreshWithdrawalsWithNames() {
        val list = firebaseHelper.fetchWithdrawRequestsWithUserNames()
        withdrawDao.deleteAllWithNames()
        withdrawDao.insertAllWithNames(list)
    }

    suspend fun clearAll() {
        withdrawDao.deleteAll()
    }

    suspend fun deleteByStatus(status: String) {
        withdrawDao.deleteByStatus(status)
    }
}
