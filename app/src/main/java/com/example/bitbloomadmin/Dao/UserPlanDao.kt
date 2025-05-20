package com.example.bitbloomadmin.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bitbloomadmin.models.UserPlanEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for querying user_plan_table using Flow for reactive streams.
 */
@Dao
interface UserPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<UserPlanEntity>)

    @Query(
        """
        SELECT * FROM user_plan_table
        WHERE planStatus = 'active'
          AND startDateMillis BETWEEN :fromMillis AND :toMillis
        """
    )
    fun getActivePlansBetween(
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<UserPlanEntity>>
}