package com.example.bitbloomadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.databinding.ItemTicketsBinding
import com.example.bitbloomadmin.models.SupportTicket
import com.example.bitbloomadmin.utils.TicketStatus

// TicketAdapter.kt  (put in a shared package so both apps can reuse)
class TicketAdapter(
    private val onClick: (SupportTicket) -> Unit
) : ListAdapter<SupportTicket, TicketAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<SupportTicket>() {
        override fun areItemsTheSame(a: SupportTicket, b: SupportTicket) = a.id == b.id
        override fun areContentsTheSame(a: SupportTicket, b: SupportTicket) = a == b
    }

    inner class VH(private val vb: ItemTicketsBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(t: SupportTicket) = with(vb) {
            userId.text = t.userId
            userName.text = t.username
            ticketStatus.text = t.status.replaceFirstChar { it.uppercase() }

            val color = when (t.status) {
                TicketStatus.PENDING.value -> R.color.ticket_pending
                TicketStatus.CLOSED.value -> R.color.ticket_closed
                else -> android.R.color.white
            }
            ticketStatus.setTextColor(ContextCompat.getColor(root.context, color))

            root.setOnClickListener { onClick(t) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        VH(ItemTicketsBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, p: Int) = h.bind(getItem(p))
}
