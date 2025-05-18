package com.example.bitbloomadmin.Repository

import com.bitbloom.bitbloomadmin.Dao.UserDao
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
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
        userDao.insertAllUsers(users)
        userDao.insertAllAccounts(accounts)
    }

    fun getUsersWithAccounts(): Flow<List<UserWithAccount>> {
        return combine(
            userDao.getAllUsers(),
            userDao.getAllAccounts()
        ) { users, accounts ->
            val accountMap = accounts.associateBy { it.userId }
            users.map { user ->
                UserWithAccount(
                    user.toString(), accountMap[user.userId].toString(),
                    totalDeposit = TODO(),
                    currentBalance = TODO(),
                    withdraw = TODO(),
                    totalEarned = TODO()
                )
            }
        }
    }
}

