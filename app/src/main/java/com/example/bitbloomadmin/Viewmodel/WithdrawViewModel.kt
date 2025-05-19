package com.example.bitbloomadmin.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitbloomadmin.Repository.WithdrawRepository
import com.example.bitbloomadmin.models.WithdrawWithUserName
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WithdrawViewModel(private val repository: WithdrawRepository) : ViewModel() {

    val withdrawsWithNames: StateFlow<List<WithdrawWithUserName>> = repository.withdrawsWithNamesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshWithdrawalsWithNames()
        }
    }
}
