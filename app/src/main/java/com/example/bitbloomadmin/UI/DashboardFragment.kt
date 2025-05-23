package com.example.bitbloomadmin.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardFragment : BaseFragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var withdrawViewModel: WithdrawViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvActive = view.findViewById<TextView>(R.id.tv_active_users)
        val tvInactive = view.findViewById<TextView>(R.id.tv_inactive_users)
        val tvDeposit = view.findViewById<TextView>(R.id.tv_total_deposit)
        val tvWithdrawal = view.findViewById<TextView>(R.id.tv_total_withdrawal)

        // Init DB & Firebase helper
        val db = AppDatabase.getDatabase(requireContext())
        val fbHelper = FirebaseHelper(requireContext())

        // User VM
        val userRepo = UserRepository(fbHelper, db.userDao())
        userViewModel = ViewModelProvider(
            this, UserViewModelFactory(userRepo)
        )[UserViewModel::class.java]

        // Withdraw VM
        val withdrawRepo = WithdrawRepository(db.withdrawDao(), fbHelper)
        withdrawViewModel = ViewModelProvider(
            this, WithdrawViewModelFactory(withdrawRepo)
        )[WithdrawViewModel::class.java]

        // — User stats —
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.usersWithAccounts.collect { users ->
                val activeCount = users.count { it.currentBalance > 0.0 }
                val inactiveCount = users.size - activeCount
                val totalDeposit = users.sumOf { it.totalDeposit }

                tvActive.text = activeCount.toString()
                tvInactive.text = inactiveCount.toString()
                tvDeposit.text  = String.format(Locale.getDefault(), "%.2f", totalDeposit)
            }
        }

        // — Approved withdrawals only —
        viewLifecycleOwner.lifecycleScope.launch {
            withdrawViewModel.withdrawsWithNames.collect { requests ->
                val approvedSum = requests
                    .filter { it.withdraw.status.equals("approved", ignoreCase = true) }
                    .sumOf { it.withdraw.amount }

                tvWithdrawal.text = String.format(Locale.getDefault(), "%.2f", approvedSum)
            }
        }
    }
}