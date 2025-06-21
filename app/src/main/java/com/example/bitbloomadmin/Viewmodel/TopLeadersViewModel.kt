package com.example.bitbloomadmin.Viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitbloomadmin.Repository.TopLeadersRepository
import kotlinx.coroutines.launch

class TopLeadersViewModel(
    private val repo: TopLeadersRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<String?>()
    private val _success = MutableLiveData<Unit?>()

    /** Exposed as LiveData for the Fragment to observe */
    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<String?> = _error
    val success: LiveData<Unit?> = _success

    /**
     * Kick off the update; updates _isLoading / _error / _success appropriately.
     */
    fun updateLeader(rank: Int, userId: String, totalBusiness: Double) {
        _isLoading.value = true
        _error.value = null
        _success.value = null

        viewModelScope.launch {
            try {
                repo.updateLeader(rank, userId, totalBusiness)
                _success.value = Unit
            } catch (t: Throwable) {
                _error.value = t.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}