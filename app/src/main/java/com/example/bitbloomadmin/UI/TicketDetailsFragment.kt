// TicketDetailsFragment.kt
package com.example.bitbloomadmin.UI

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bitbloomadmin.Repository.SupportTicketRepository
import com.example.bitbloomadmin.Viewmodel.AdminSupportVMFactory
import com.example.bitbloomadmin.Viewmodel.AdminSupportViewModel
import com.example.bitbloomadmin.databinding.FragmentTicketDetailsBinding
import com.example.bitbloomadmin.models.SupportTicket
import com.example.bitbloomadmin.utils.TicketStatus
import com.google.android.material.textfield.TextInputEditText

class TicketDetailsFragment : Fragment() {

    private var _b: FragmentTicketDetailsBinding? = null
    private val b get() = _b!!

    private val vm: AdminSupportViewModel by viewModels { AdminSupportVMFactory() }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentTicketDetailsBinding.inflate(inflater, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = requireArguments().getString("ticketId")!!

        // —— live document listener ——
        SupportTicketRepository.getInstance()
            .ticketsCollection().document(id)
            .addSnapshotListener { snap, _ ->
                snap?.toObject(SupportTicket::class.java)?.let { bind(it) }
            }

        // —— submit reply ——
        b.btnSubmit.setOnClickListener {
            val reply = b.etReply.text.toString().trim()
            if (reply.isNotBlank()) vm.submitReply(id, reply)
        }

        // Make the user-message box readonly-scrollable once (reply kept editable)
        makeReadOnlyAndScrollable(b.editMessage)
    }

    private fun bind(t: SupportTicket) = with(b) {
        etUserID.setText(t.userId)
        etEmail.setText(t.email)
        etTicketStatus.setText(t.status.replaceFirstChar { it.uppercase() })
        etSubject.setText(t.subject)
        editMessage.setText(t.message)

        val isPending = t.status == TicketStatus.PENDING.value
        etReply.setText(t.reply)
        etReply.isEnabled = isPending
        btnSubmit.isVisible = isPending
    }

    /* ---------- helpers ---------- */

    @SuppressLint("ClickableViewAccessibility")   // we call performClick() ourselves
    private fun makeReadOnlyAndScrollable(vararg boxes: TextInputEditText) {
        boxes.forEach { et ->
            // 1) Disable editing
            et.keyListener = null
            et.isLongClickable = false
            et.setTextIsSelectable(false)

            // 2) Enable internal scrolling
            et.movementMethod = ScrollingMovementMethod.getInstance()

            // 3) Stop parent ScrollView intercept while finger is on the EditText
            et.setOnTouchListener { v, ev ->
                when (ev.actionMasked) {
                    MotionEvent.ACTION_DOWN ->
                        v.parent.requestDisallowInterceptTouchEvent(true)

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        v.performClick()        // keeps accessibility happy
                    }
                }
                false   // let EditText handle the actual scroll
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _b = null
    }
}