package com.example.bitbloomadmin.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<UserModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAccounts(accounts: List<AccountModel>)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<UserModel>>

    @Query("SELECT * FROM account_table")
    fun getAllAccounts(): Flow<List<AccountModel>>

    @Query("DELETE FROM user_table")
    suspend fun clearAllUsers()

    @Query("DELETE FROM account_table")
    suspend fun clearAllAccounts()
}


