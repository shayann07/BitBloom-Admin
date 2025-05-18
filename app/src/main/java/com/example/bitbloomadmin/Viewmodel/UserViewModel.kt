package com.example.bitbloomadmin.Viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.models.AccountModel
import com.example.bitbloomadmin.models.AnnouncementModel
import com.example.bitbloomadmin.models.UserModel
import com.example.bitbloomadmin.models.UserWithAccount
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val usersWithAccounts: StateFlow<List<UserWithAccount>> = repository.getUsersWithAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun syncNow() {
        viewModelScope.launch {
            repository.syncFromFirebase()
        }
    }
}
