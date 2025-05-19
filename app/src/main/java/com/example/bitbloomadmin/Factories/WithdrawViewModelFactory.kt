package com.example.bitbloomadmin.Factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bitbloomadmin.Repository.WithdrawRepository
import com.example.bitbloomadmin.Viewmodel.WithdrawViewModel

class WithdrawViewModelFactory(
    private val repository: WithdrawRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WithdrawViewModel::class.java)) {
            return WithdrawViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
