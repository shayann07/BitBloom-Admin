package com.example.bitbloomadmin.Viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.models.AnnouncementModel
import com.example.bitbloomadmin.models.UserWithAccount
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun addAnnouncement(announcement: AnnouncementModel) {
        repository.addAnnouncement(announcement)
    }

    private val _announcements = MutableStateFlow<List<AnnouncementModel>>(emptyList())
    val announcements: StateFlow<List<AnnouncementModel>> = _announcements

    fun fetchAnnouncements() {
        repository.fetchAnnouncements(
            onSuccess = { list ->
                _announcements.value = list
            },
            onFailure = { error ->
                Log.e("UserViewModel", "Failed to fetch announcements", error)
            }
        )
    }

}
