package com.example.bitbloomadmin.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bitbloomadmin.Repository.TopLeadersRepository

class TopLeadersVMFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopLeadersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopLeadersViewModel(TopLeadersRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}