package com.example.bitbloomadmin.models

import com.example.bitbloomadmin.utils.TicketStatus
import com.google.firebase.Timestamp

data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val deviceToken: String = "",
    val subject: String = "",
    val message: String = "",
    val reply: String = "",
    val status: String = TicketStatus.PENDING.value,
    val reminded: Boolean = false,
    val createdAt: Timestamp? = null,
    val answeredAt: Timestamp? = null,
    val closedAt: Timestamp? = null
)