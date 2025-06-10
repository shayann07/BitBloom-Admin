package com.example.bitbloomadmin.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.ReportViewModelFactory
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.ReportRepository
import com.example.bitbloomadmin.Viewmodel.ReportViewModel
import com.example.bitbloomadmin.utils.TimeFilter
import kotlinx.coroutines.launch

class ReportFragment : BaseFragment() {

    private lateinit var viewModel: ReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_report, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // Init ViewModel
        val db           = AppDatabase.getDatabase(requireContext())
        val firebaseHelper = FirebaseHelper(requireContext())
        val repo         = ReportRepository(
            firebaseHelper,
            db.userDao(),
            db.userPlanDao(),
            db.withdrawDao()
        )
        val factory      = ReportViewModelFactory(repo)
        viewModel        = ViewModelProvider(this, factory)[ReportViewModel::class.java]

        // Bind views
        val tvTotalUsers     = view.findViewById<TextView>(R.id.tv_total_users)
        val tvActiveUsers    = view.findViewById<TextView>(R.id.tv_active_users)
        val tvInactiveUsers  = view.findViewById<TextView>(R.id.tv_inactive_users)
        val tvBlockedUsers   = view.findViewById<TextView>(R.id.tv_blocked_users)
        val tvNewUsers       = view.findViewById<TextView>(R.id.tv_new_users)
        val tvAdminUsers     = view.findViewById<TextView>(R.id.tv_admin_users)
        val tvTotalDeposit   = view.findViewById<TextView>(R.id.tv_total_deposit)
        val tvTotalInvested  = view.findViewById<TextView>(R.id.tv_total_invested)
        val tvReq            = view.findViewById<TextView>(R.id.tv_total_withdrawal_requests)
        val tvApprovedReq    = view.findViewById<TextView>(R.id.tv_approved_withdrawal_requests)
        val tvPendingReq     = view.findViewById<TextView>(R.id.tv_pending_withdrawal_requests)
        val tvRejectedReq    = view.findViewById<TextView>(R.id.tv_rejected_withdrawal_requests)
        val tvApprovedAmt    = view.findViewById<TextView>(R.id.tv_approved_withdrawal_amount)
        val tvPendingAmt     = view.findViewById<TextView>(R.id.tv_pending_withdrawal_amount)
        val tvRoiEarnings    = view.findViewById<TextView>(R.id.tv_total_roi_earnings)
        val tvReferralEarnings = view.findViewById<TextView>(R.id.tv_total_referral_earnings)
        val tvTeamEarnings   = view.findViewById<TextView>(R.id.tv_total_team_earnings)

        // Show loader until first batch of data binds
        showLoading()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.totalUsers.collect     { tvTotalUsers.text     = it.toString() } }
                launch { viewModel.activeUsers.collect    { tvActiveUsers.text    = it.toString() } }
                launch { viewModel.inactiveUsers.collect  { tvInactiveUsers.text  = it.toString() } }
                launch { viewModel.blockedUsers.collect   { tvBlockedUsers.text   = it.toString() } }
                launch { viewModel.newUsers.collect       { tvNewUsers.text       = it.toString() } }
                launch { viewModel.adminCreatedUsers.collect { tvAdminUsers.text  = it.toString() } }
                launch { viewModel.totalDeposited.collect { tvTotalDeposit.text   = String.format("%.2f", it) } }
                launch { viewModel.totalInvested.collect { tvTotalInvested.text  = String.format("%.2f", it) } }
                launch { viewModel.withdrawalRequests.collect  { tvReq.text         = it.toString() } }
                launch { viewModel.approvedRequests.collect    { tvApprovedReq.text = it.toString() } }
                launch { viewModel.pendingRequests.collect     { tvPendingReq.text  = it.toString() } }
                launch { viewModel.rejectedRequests.collect    { tvRejectedReq.text = it.toString() } }
                launch { viewModel.approvedAmount.collect      { tvApprovedAmt.text = String.format("%.2f", it) } }
                launch { viewModel.pendingAmount.collect       { tvPendingAmt.text  = String.format("%.2f", it) } }
                launch { viewModel.totalROIEarnings.collect    { tvRoiEarnings.text = String.format("%.2f", it) } }
                launch { viewModel.totalReferralEarnings.collect { tvReferralEarnings.text = String.format("%.2f", it) } }
                launch { viewModel.totalTeamEarnings.collect   { tvTeamEarnings.text = String.format("%.2f", it) } }
            }
            hideLoading()
        }

        // Filter button
        view.findViewById<ImageButton>(R.id.btn_filter).setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.material_dialog, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create().apply { window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT)) }
            dialog.show()

            val mapping = mapOf(
                R.id.item_today        to TimeFilter.TODAY,
                R.id.item_last_7_days  to TimeFilter.WEEKLY,
                R.id.item_last_30_days to TimeFilter.MONTHLY,
                R.id.item_last_365_days to TimeFilter.ANNUALLY,
                R.id.item_all_time     to TimeFilter.ALL_TIME
            )
            mapping.forEach { (viewId, filterType) ->
                dialogView.findViewById<TextView>(viewId).setOnClickListener {
                    viewModel.setFilter(filterType)
                    dialog.dismiss()
                }
            }
        }
    }
}
