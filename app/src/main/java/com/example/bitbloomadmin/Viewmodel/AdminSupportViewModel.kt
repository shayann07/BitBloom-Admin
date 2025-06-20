package com.example.bitbloomadmin.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bitbloomadmin.Repository.SupportTicketRepository
import com.example.bitbloomadmin.models.SupportTicket
import com.example.bitbloomadmin.utils.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminSupportViewModel(
    private val repo: SupportTicketRepository = SupportTicketRepository.getInstance()
) : ViewModel() {

    private val _all = MutableStateFlow<List<SupportTicket>>(emptyList())
    val all: StateFlow<List<SupportTicket>> = _all

    val pendingCount = MutableStateFlow(0)
    val closedCount = MutableStateFlow(0)
    val answeredCount = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            repo.allTickets().collect { list ->
                _all.value = list
                pendingCount.value = list.count { it.status == TicketStatus.PENDING.value }
                closedCount.value = list.count { it.status == TicketStatus.CLOSED.value }
                answeredCount.value = closedCount.value            // same data set
            }
        }
    }

    fun submitReply(id: String, reply: String) =
        repo.replyToTicket(id, reply)
}

// AdminSupportVMFactory.kt
class AdminSupportVMFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AdminSupportViewModel() as T
}