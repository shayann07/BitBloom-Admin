package com.bitbloom.bitbloomadmin.Dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bitbloomadmin.models.WithdrawModel

@Dao
interface WithdrawDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawRequest(withdraw: WithdrawModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(withdraws: List<WithdrawModel>)


    @Query("SELECT * FROM withdrawals")
    fun getAllWithdrawRequests(): LiveData<List<WithdrawModel>>


    @Query("DELETE FROM withdrawals")
    suspend fun deleteAll()

    @Query("DELETE FROM withdrawals WHERE status = :status")
    suspend fun deleteByStatus(status: String)
}

