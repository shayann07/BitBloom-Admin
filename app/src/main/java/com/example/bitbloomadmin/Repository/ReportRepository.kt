// com/example/bitbloomadmin/Repository/ReportRepository.kt
package com.example.bitbloomadmin.Repository

import com.example.bitbloomadmin.Dao.UserDao
import com.example.bitbloomadmin.Dao.UserPlanDao
import com.example.bitbloomadmin.Dao.WithdrawDao
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.models.UserPlanEntity
import com.example.bitbloomadmin.utils.TimeFilter
import kotlinx.coroutines.flow.first
import java.util.Calendar

class ReportRepository(
    private val firebaseHelper: FirebaseHelper,
    private val userDao: UserDao,                  // getAllUsers() :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
    private val userPlanDao: UserPlanDao,          // getActivePlansBetween() :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}
    private val withdrawDao: WithdrawDao           // getAllWithdrawRequestsFlow() :contentReference[oaicite:4]{index=4}:contentReference[oaicite:5]{index=5}
) {

    // -- SYNC userPlans from Firestore into Room --------------------------------

    suspend fun syncUserPlans() {
        val planModels = firebaseHelper.getUserPlansFlow().first()
        val entities = planModels.map { UserPlanEntity.fromModel(it) }
        userPlanDao.insertAll(entities)
    }

    // -- TIME RANGE UTILITIES ---------------------------------------------------

    private fun computeRange(filter: TimeFilter): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val start = when (filter) {
            TimeFilter.TODAY -> Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            TimeFilter.WEEKLY -> now - 7L * 24 * 60 * 60 * 1000
            TimeFilter.MONTHLY -> now - 30L * 24 * 60 * 60 * 1000
            TimeFilter.ANNUALLY -> now - 365L * 24 * 60 * 60 * 1000
            TimeFilter.ALL_TIME -> 0L
        }
        return start to now
    }

    // -- USER STATS -------------------------------------------------------------

    suspend fun countTotalUsers(): Int = userDao.getAllUsers().first().size

    suspend fun countActiveUsers(): Int =
        userDao.getAllUsers().first().count { it.status == "active" }

    suspend fun countInactiveUsers(): Int =
        userDao.getAllUsers().first().count { it.status == "inactive" }

    suspend fun countNewUsers(filter: TimeFilter): Int {
        val (from, to) = computeRange(filter)
        return userDao.getAllUsers().first().count {
            val ts = it.createdAt.toDate().time
            ts in from..to
        }
    }

    suspend fun countBlockedUsers(): Int =
        userDao.getAllUsers().first()
            .count { it.isBlocked }

    suspend fun countAdminCreatedUsers(filter: TimeFilter): Int {
        val (from, to) = computeRange(filter)
        return userDao.getAllUsers().first().count {
            it.createdByAdmin && run { val ts = it.createdAt.toDate().time; ts in from..to }
        }
    }

    // -- FINANCIAL STATS --------------------------------------------------------

    /** Sum of all deposits, never filtered by time */
    suspend fun sumTotalDeposited(): Double =
        userDao.getAllAccounts().first().sumOf { it.investment.total_deposit }

    /** Sum of invested_amount for active plans in the time window */
    suspend fun sumInvested(filter: TimeFilter): Double {
        val (from, to) = computeRange(filter)
        val plans = userPlanDao.getActivePlansBetween(from, to).first()
        return plans.sumOf { it.investedAmount }
    }

    /** Count of all withdrawal requests in window */
    suspend fun countWithdrawalRequests(filter: TimeFilter): Int {
        val (from, to) = computeRange(filter)
        return withdrawDao.getAllWithdrawRequestsFlow().first().count {
            val ts = it.timestamp.toDate().time
            ts in from..to
        }
    }

    /** Count of withdrawal requests by status in window */
    suspend fun countWithdrawalRequestsByStatus(filter: TimeFilter, status: String): Int {
        val (from, to) = computeRange(filter)
        return withdrawDao.getAllWithdrawRequestsFlow().first().count {
            it.status == status && run { val ts = it.timestamp.toDate().time; ts in from..to }
        }
    }

    /** Sum of withdrawal amounts by status in window */
    suspend fun sumWithdrawalAmountsByStatus(filter: TimeFilter, status: String): Double {
        val (from, to) = computeRange(filter)
        return withdrawDao.getAllWithdrawRequestsFlow().first().filter { it.status == status }
            .sumOf { it.amount }
    }

    /** Convenience for approved/pending counts & sums */
    suspend fun countApprovedWithdrawals(filter: TimeFilter) =
        countWithdrawalRequestsByStatus(filter, "approved")

    suspend fun countPendingWithdrawals(filter: TimeFilter) =
        countWithdrawalRequestsByStatus(filter, "pending")

    suspend fun countRejectedWithdrawals(filter: TimeFilter) =
        countWithdrawalRequestsByStatus(filter, "rejected")

    suspend fun sumApprovedWithdrawalAmount(filter: TimeFilter) =
        sumWithdrawalAmountsByStatus(filter, "approved")

    suspend fun sumPendingWithdrawalAmount(filter: TimeFilter) =
        sumWithdrawalAmountsByStatus(filter, "pending")

    // -- EARNINGS INSIGHTS (full-history sums) ----------------------------------

    suspend fun sumTotalROIEarnings(): Double =
        userDao.getAllAccounts().first().sumOf { it.earnings.lifetime_roi_income }

    suspend fun sumTotalReferralEarnings(): Double =
        userDao.getAllAccounts().first().sumOf { it.earnings.lifetime_referral_income }

    suspend fun sumTotalTeamEarnings(): Double =
        userDao.getAllAccounts().first().sumOf { it.earnings.lifetime_team_income }
}
