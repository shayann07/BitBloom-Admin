package com.example.bitbloomadmin.Repository

import com.example.bitbloomadmin.models.SupportTicket
import com.example.bitbloomadmin.utils.TicketStatus
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class SupportTicketRepository private constructor() {

    private val col = FirebaseFirestore.getInstance().collection("tickets")

    /* ---------- WRITE ---------- */

    fun replyToTicket(id: String, reply: String) =
        col.document(id).update(
            mapOf(
                "reply" to reply,
                "status" to TicketStatus.CLOSED.value,
                "answeredAt" to FieldValue.serverTimestamp(),
                "closedAt" to FieldValue.serverTimestamp()
            )
        )

    /* ---------- READ ---------- */

    fun allTickets() = col.orderBy("createdAt", Query.Direction.DESCENDING).asFlow()
    fun ticketsByStatus(status: String) =
        col.whereEqualTo("status", status).orderBy("createdAt", Query.Direction.DESCENDING).asFlow()

    /* ---------- singleton ---------- */
    companion object {
        private var i: SupportTicketRepository? = null
        fun getInstance() = i ?: SupportTicketRepository().also { i = it }
    }

    private fun Query.asFlow() = callbackFlow<List<SupportTicket>> {
        val reg = addSnapshotListener { s, _ ->
            trySend(s?.toObjects(SupportTicket::class.java) ?: emptyList())
        }; awaitClose { reg.remove() }
    }

    fun ticketsCollection() = col
}