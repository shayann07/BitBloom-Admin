package com.example.bitbloomadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.WithdrawViewModelFactory
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Repository.WithdrawRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.Viewmodel.WithdrawViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var withdrawViewModel: WithdrawViewModel

    private var isStatsLoaded = false
    private var isWithdrawLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        val tvActive     = view.findViewById<TextView>(R.id.tv_active_users)
        val tvInactive   = view.findViewById<TextView>(R.id.tv_inactive_users)
        val tvDeposit    = view.findViewById<TextView>(R.id.tv_total_deposit)
        val tvWithdrawal = view.findViewById<TextView>(R.id.tv_total_withdrawal)

        // Init VMs (no automatic clear in sync)
        val db       = AppDatabase.getDatabase(requireContext())
        val fbHelper = FirebaseHelper(requireContext())
        userViewModel = ViewModelProvider(
            this, UserViewModelFactory(UserRepository(fbHelper, db.userDao()))
        )[UserViewModel::class.java]
        withdrawViewModel = ViewModelProvider(
            this, WithdrawViewModelFactory(WithdrawRepository(db.withdrawDao(), fbHelper))
        )[WithdrawViewModel::class.java]

        // 1) Show loader until Room emits real data (non-empty)
        showLoading()
        lifecycleScope.launch {
            combine(
                userViewModel.usersWithAccounts
                    .filter { it.isNotEmpty() },     // wait for at least one user
                withdrawViewModel.withdrawsWithNames
                    .filter { it.isNotEmpty() }      // wait for at least one withdraw
            ) { _, _ -> Unit }
                .first() // suspend until both flows have a non-empty list
            hideLoading()
        }

        // 2) Collect users from Room immediately
        lifecycleScope.launch {
            userViewModel.usersWithAccounts.collect { users ->
                val activeCount   = users.count { it.currentBalance > 0.0 }
                val inactiveCount = users.size - activeCount
                val totalDeposit  = users.sumOf { it.totalDeposit }

                tvActive.text   = activeCount.toString()
                tvInactive.text = inactiveCount.toString()
                tvDeposit.text  = String.format(Locale.getDefault(), "%.2f", totalDeposit)
            }
        }

        // 3) Collect withdraws from Room immediately
        lifecycleScope.launch {
            withdrawViewModel.withdrawsWithNames.collect { requests ->
                val approvedSum = requests
                    .filter { it.withdraw.status.equals("approved", true) }
                    .sumOf { it.withdraw.amount }
                tvWithdrawal.text = String.format(Locale.getDefault(), "%.2f", approvedSum)
            }
        }

        // 4) Trigger background syncs
        userViewModel.syncNow()
        withdrawViewModel.refreshData()
    }
}
