package com.example.bitbloomadmin.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.UserModel
import com.example.bitbloomadmin.models.WithdrawModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A Worker that synchronizes Firestore → Room for Users, Withdrawals, and Accounts.
 * Runs on Dispatchers.IO and logs any failures.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "SyncWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val context = applicationContext

            // 1) Instantiate Firestore helper and DAOs
            val firebaseHelper = FirebaseHelper(context)
            val db             = AppDatabase.getDatabase(context)
            val userDao        = db.userDao()
            val withdrawDao    = db.withdrawDao()

            // 2) Fetch all users from Firestore
            val userModels: List<UserModel> = firebaseHelper.fetchUsers()
            Log.d(TAG, "Fetched ${userModels.size} users from Firestore")

            // 3) Clear local users table and insert fresh
            userDao.clearAllUsers()
            userDao.insertAllUsers(userModels)

            // 4) Fetch all withdraw requests (as WithdrawModel)
            val withdrawList: List<WithdrawModel> =
                firebaseHelper.fetchWithdrawRequestsWithUserNames()
                    .map { it.withdraw } // extract WithdrawModel
            Log.d(TAG, "Fetched ${withdrawList.size} withdrawal requests")

            // 5) Clear local withdrawals table and insert fresh
            withdrawDao.deleteAll()
            withdrawDao.insertAll(withdrawList)

            // 6) Fetch all accounts from Firestore
            val accountList: List<AccountModel> = firebaseHelper.fetchAccounts()
            Log.d(TAG, "Fetched ${accountList.size} accounts from Firestore")

            // 7) Clear local accounts table and insert fresh
            userDao.clearAllAccounts()
            userDao.insertAllAccounts(accountList)

            // 8) Success
            Log.d(TAG, "SyncWorker: sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "SyncWorker failed", e)
            // If it’s a transient error (e.g., network), retry later
            Result.retry()
        }
    }
}
