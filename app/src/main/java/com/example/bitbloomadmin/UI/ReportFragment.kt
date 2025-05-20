package com.example.bitbloomadmin.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.ReportViewModelFactory
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.ReportRepository
import com.example.bitbloomadmin.Viewmodel.ReportViewModel
import com.example.bitbloomadmin.utils.TimeFilter
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {
    private lateinit var viewModel: ReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize dependencies
        val db = AppDatabase.getDatabase(requireContext())
        val firebaseHelper = FirebaseHelper(requireContext())
        val repository = ReportRepository(
            firebaseHelper,
            db.userDao(),
            db.userPlanDao(),
            db.withdrawDao()
        )

        // Create ViewModel
        val factory = ReportViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ReportViewModel::class.java)

        // Bind views
        val tvTotalUsers = view.findViewById<TextView>(R.id.tv_total_users)
        val tvActiveUsers = view.findViewById<TextView>(R.id.tv_active_users)
        val tvInactiveUsers = view.findViewById<TextView>(R.id.tv_inactive_users)
        val tvBlockedUsers = view.findViewById<TextView>(R.id.tv_blocked_users)
        val tvNewUsers = view.findViewById<TextView>(R.id.tv_new_users)
        val tvAdminUsers = view.findViewById<TextView>(R.id.tv_admin_users)

        val tvTotalDeposit = view.findViewById<TextView>(R.id.tv_total_deposit)
        val tvTotalInvested = view.findViewById<TextView>(R.id.tv_total_invested)
        val tvReq = view.findViewById<TextView>(R.id.tv_total_withdrawal_requests)
        val tvApprovedReq = view.findViewById<TextView>(R.id.tv_approved_withdrawal_requests)
        val tvPendingReq = view.findViewById<TextView>(R.id.tv_pending_withdrawal_requests)
        val tvRejectedReq = view.findViewById<TextView>(R.id.tv_rejected_withdrawal_requests)
        val tvApprovedAmt = view.findViewById<TextView>(R.id.tv_approved_withdrawal_amount)
        val tvPendingAmt = view.findViewById<TextView>(R.id.tv_pending_withdrawal_amount)

        val tvRoiEarnings = view.findViewById<TextView>(R.id.tv_total_roi_earnings)
        val tvReferralEarnings = view.findViewById<TextView>(R.id.tv_total_referral_earnings)
        val tvTeamEarnings = view.findViewById<TextView>(R.id.tv_total_team_earnings)

        // Observe flows
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.totalUsers.collect { tvTotalUsers.text = it.toString() } }
                launch { viewModel.activeUsers.collect { tvActiveUsers.text = it.toString() } }
                launch { viewModel.inactiveUsers.collect { tvInactiveUsers.text = it.toString() } }
                launch { viewModel.blockedUsers.collect { tvBlockedUsers.text = it.toString() } }
                launch { viewModel.newUsers.collect { tvNewUsers.text = it.toString() } }
                launch { viewModel.adminCreatedUsers.collect { tvAdminUsers.text = it.toString() } }

                launch {
                    viewModel.totalDeposited.collect {
                        tvTotalDeposit.text = String.format("%.2f", it)
                    }
                }
                launch {
                    viewModel.totalInvested.collect {
                        tvTotalInvested.text = String.format("%.2f", it)
                    }
                }
                launch { viewModel.withdrawalRequests.collect { tvReq.text = it.toString() } }
                launch { viewModel.approvedRequests.collect { tvApprovedReq.text = it.toString() } }
                launch { viewModel.pendingRequests.collect { tvPendingReq.text = it.toString() } }
                launch { viewModel.rejectedRequests.collect { tvRejectedReq.text = it.toString() } }
                launch {
                    viewModel.approvedAmount.collect {
                        tvApprovedAmt.text = String.format("%.2f", it)
                    }
                }
                launch {
                    viewModel.pendingAmount.collect {
                        tvPendingAmt.text = String.format("%.2f", it)
                    }
                }

                launch {
                    viewModel.totalROIEarnings.collect {
                        tvRoiEarnings.text = String.format("%.2f", it)
                    }
                }
                launch {
                    viewModel.totalReferralEarnings.collect {
                        tvReferralEarnings.text = String.format("%.2f", it)
                    }
                }
                launch {
                    viewModel.totalTeamEarnings.collect {
                        tvTeamEarnings.text = String.format("%.2f", it)
                    }
                }
            }
        }

        // Filter button
        view.findViewById<ImageButton>(R.id.btn_filter).setOnClickListener {
            val items = arrayOf(
                "Today",
                "Last 7 Days",
                "Last 30 Days",
                "Last 365 Days",
                "All Time"
            )
            AlertDialog.Builder(requireContext())
                .setTitle("Select Time Filter")
                .setItems(items) { _, which ->
                    val filter = when (which) {
                        0 -> TimeFilter.TODAY
                        1 -> TimeFilter.WEEKLY
                        2 -> TimeFilter.MONTHLY
                        3 -> TimeFilter.ANNUALLY
                        else -> TimeFilter.ALL_TIME
                    }
                    viewModel.setFilter(filter)
                }
                .show()
        }
    }
}
