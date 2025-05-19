package com.example.bitbloomadmin.Repository

import com.example.bitbloomadmin.Dao.UserDao
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.models.AnnouncementModel
import com.example.bitbloomadmin.models.UserWithAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


class UserRepository(
    private val firebaseHelper: FirebaseHelper,
    private val userDao: UserDao
) {

    suspend fun syncFromFirebase() {
        val users = firebaseHelper.fetchUsers()
        val accounts = firebaseHelper.fetchAccounts()

        userDao.clearAllUsers()
        userDao.clearAllAccounts()

        userDao.insertAllUsers(users)
        userDao.insertAllAccounts(accounts)

        println("✅ [Repo] Synced ${users.size} users & ${accounts.size} accounts into Room")
    }


    fun getUsersWithAccounts(): Flow<List<UserWithAccount>> {
        return combine(
            userDao.getAllUsers(),
            userDao.getAllAccounts()
        ) { users, accounts ->

            val userMap = users.associateBy { it.id }

            val merged = accounts.mapNotNull { acc ->
                val user = userMap[acc.userId]

                if (user == null) {
                    println("⚠️ No match: acc.userId = ${acc.userId} not found in userMap.keys = ${userMap.keys.take(5)}...") // sample keys
                }
                user?.let {
                    UserWithAccount(
                        name = it.name,
                        userId = it.id,
                        email = it.email,
                        phone = it.phoneNumber,
                        password = it.password,
                        referalCode = it.referralCode,
                        accountId = acc.accountId,
                        totalDeposit = acc.investment.total_deposit,
                        currentBalance = acc.investment.remaining_balance,
                        withdraw = acc.earnings.buying_profit,
                        totalEarned = acc.earnings.total_earned,
                        lifetime_referral_income = acc.earnings.lifetime_referral_income,
                        lifetime_roi_income = acc.earnings.lifetime_roi_income,
                        lifetime_team_income = acc.earnings.lifetime_team_income,
                    )
                }
            }

            println("✅ [Repo] Merged ${merged.size} users with accounts")
            merged
        }
    }

  fun addAnnouncement(announcement: AnnouncementModel) {
      firebaseHelper.addAnnouncement(announcement)
  }

}

