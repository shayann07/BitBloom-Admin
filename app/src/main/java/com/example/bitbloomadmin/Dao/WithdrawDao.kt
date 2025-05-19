package com.example.bitbloomadmin.Dao

import androidx.room.*
import com.example.bitbloomadmin.models.WithdrawModel
import com.example.bitbloomadmin.models.WithdrawWithUserName
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawRequest(withdraw: WithdrawModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(withdraws: List<WithdrawModel>)

    @Query("SELECT * FROM withdrawals")
    fun getAllWithdrawRequestsFlow(): Flow<List<WithdrawModel>>

    @Query("DELETE FROM withdrawals")
    suspend fun deleteAll()

    @Query("DELETE FROM withdrawals WHERE status = :status")
    suspend fun deleteByStatus(status: String)

    // ✅ NEW FOR USERNAME SUPPORT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWithNames(list: List<WithdrawWithUserName>)

    @Query("DELETE FROM withdraws_with_user")
    suspend fun deleteAllWithNames()

    @Query("SELECT * FROM withdraws_with_user")
    fun getAllWithdrawsWithUserName(): Flow<List<WithdrawWithUserName>>
}
