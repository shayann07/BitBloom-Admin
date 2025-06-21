package com.example.bitbloomadmin.UI


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Viewmodel.AdminSupportVMFactory
import com.example.bitbloomadmin.Viewmodel.AdminSupportViewModel
import com.example.bitbloomadmin.adapter.TicketAdapter
import com.example.bitbloomadmin.databinding.FragmentPendingBinding
import com.example.bitbloomadmin.models.SupportTicket
import com.example.bitbloomadmin.ui.BaseFragment
import com.example.bitbloomadmin.utils.TicketStatus

abstract class TicketListFragment : BaseFragment() {

    abstract val filter: (SupportTicket) -> Boolean
    abstract val screenTitle: String

    private var _b: FragmentPendingBinding? = null
    private val b get() = _b!!

    private val vm: AdminSupportViewModel by viewModels { AdminSupportVMFactory() }
    private lateinit var adapter: TicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentPendingBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        b.tvTickets.text = screenTitle

        adapter = TicketAdapter { ticket ->
            findNavController().navigate(
                R.id.ticketDetailsFragment,
                bundleOf("ticketId" to ticket.id)
            )
        }
        b.rvTickets.layoutManager = LinearLayoutManager(requireContext())
        b.rvTickets.adapter = adapter
        showLoading()

        var first = true
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.all.collect { list ->
                if (first) {
                    hideLoading()
                    first = false
                }
                val shown = list.filter(filter)
                adapter.submitList(shown)
                b.rvTickets.isVisible = shown.isNotEmpty()
                b.tvEmpty.isVisible = shown.isEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

/* --- concrete screens --- */
class PendingFragment : TicketListFragment() {
    override val filter = { t: SupportTicket -> t.status == TicketStatus.PENDING.value }
    override val screenTitle = "Pending Tickets"
}

class ClosedFragment : TicketListFragment() {
    override val filter = { t: SupportTicket -> t.status == TicketStatus.CLOSED.value }
    override val screenTitle = "Closed Tickets"
}

class AnsweredFragment : TicketListFragment() {
    override val filter = { t: SupportTicket -> t.status == TicketStatus.CLOSED.value }
    override val screenTitle = "Answered Tickets"
}

class AllFragment : TicketListFragment() {
    override val filter = { _: SupportTicket -> true }
    override val screenTitle = "All Tickets"
}