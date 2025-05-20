package com.example.bitbloomadmin.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitbloomadmin.Repository.ReportRepository
import com.example.bitbloomadmin.utils.TimeFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel exposing report metrics as StateFlows driven by a TimeFilter.
 */
class ReportViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    // Current time filter
    private val _filter = MutableStateFlow(TimeFilter.ALL_TIME)
    val filter: StateFlow<TimeFilter> = _filter.asStateFlow()


    /**
     * Update the current filter; triggers recomputation of all metrics.
     */
    fun setFilter(timeFilter: TimeFilter) {
        _filter.value = timeFilter
    }

    // -- User Stats --
    @OptIn(ExperimentalCoroutinesApi::class)
    val totalUsers: StateFlow<Int> =
        filter.flatMapLatest { flow { emit(repository.countTotalUsers()) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeUsers: StateFlow<Int> =
        filter.flatMapLatest { flow { emit(repository.countActiveUsers()) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val inactiveUsers: StateFlow<Int> =
        filter.flatMapLatest { flow { emit(repository.countInactiveUsers()) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val newUsers: StateFlow<Int> =
        filter.flatMapLatest { f -> flow { emit(repository.countNewUsers(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val blockedUsers: StateFlow<Int> = filter
        .flatMapLatest { flow { emit(repository.countBlockedUsers()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val adminCreatedUsers: StateFlow<Int> =
        filter.flatMapLatest { f -> flow { emit(repository.countAdminCreatedUsers(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // -- Financial Stats --
    val totalDeposited: StateFlow<Double> = flow {
        emit(repository.sumTotalDeposited())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalInvested: StateFlow<Double> =
        filter.flatMapLatest { f -> flow { emit(repository.sumInvested(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val withdrawalRequests: StateFlow<Int> =
        filter.flatMapLatest { f -> flow { emit(repository.countWithdrawalRequests(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val approvedRequests: StateFlow<Int> =
        filter.flatMapLatest { f -> flow { emit(repository.countApprovedWithdrawals(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pendingRequests: StateFlow<Int> =
        filter.flatMapLatest { f -> flow { emit(repository.countPendingWithdrawals(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val rejectedRequests: StateFlow<Int> =
        filter.flatMapLatest { f -> flow { emit(repository.countRejectedWithdrawals(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val approvedAmount: StateFlow<Double> =
        filter.flatMapLatest { f -> flow { emit(repository.sumApprovedWithdrawalAmount(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pendingAmount: StateFlow<Double> =
        filter.flatMapLatest { f -> flow { emit(repository.sumPendingWithdrawalAmount(f)) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // -- Earnings Insights --
    val totalROIEarnings: StateFlow<Double> = flow {
        emit(repository.sumTotalROIEarnings())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalReferralEarnings: StateFlow<Double> = flow {
        emit(repository.sumTotalReferralEarnings())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalTeamEarnings: StateFlow<Double> = flow {
        emit(repository.sumTotalTeamEarnings())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        // Sync user plans when ViewModel is created
        viewModelScope.launch {
            repository.syncUserPlans()
        }
    }
}