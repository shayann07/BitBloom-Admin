package com.example.bitbloomadmin.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitbloomadmin.Repository.WithdrawRepository
import com.example.bitbloomadmin.Factories.WithdrawViewModelFactory
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Viewmodel.WithdrawViewModel
import com.example.bitbloomadmin.adapter.WithdrawAdapter
import com.example.bitbloomadmin.databinding.FragmentWithdrawBinding
import com.example.bitbloomadmin.models.UserWithAccount
import com.example.bitbloomadmin.models.WithdrawWithUserName
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.notifications.AccessToken
import com.example.bitbloomadmin.notifications.Fcm
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.SearchView

import kotlinx.coroutines.launch

class WithdrawFragment : BaseFragment(), WithdrawAdapter.WithdrawHandler {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WithdrawViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: WithdrawAdapter
    private val firestore = FirebaseFirestore.getInstance()

    // New: full and filtered lists for search
    private var allWithdrawals: List<WithdrawWithUserName> = emptyList()
    private var filteredWithdrawals: List<WithdrawWithUserName> = emptyList()

    private lateinit var deviceToken: String
    private var cachedUserList: List<UserWithAccount> = emptyList()
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)

        // Setup DAOs & ViewModels
        val withdrawDao = AppDatabase.getDatabase(requireContext()).withdrawDao()
        val userDao     = AppDatabase.getDatabase(requireContext()).userDao()
        val helper      = FirebaseHelper(requireContext())

        val withdrawRepo  = WithdrawRepository(withdrawDao, helper)
        viewModel         = ViewModelProvider(this, WithdrawViewModelFactory(withdrawRepo))
            .get(WithdrawViewModel::class.java)

        val userRepo   = UserRepository(helper, userDao)
        userViewModel  = ViewModelProvider(this, UserViewModelFactory(userRepo))
            .get(UserViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // Recycler setup
        adapter = WithdrawAdapter(handler = this)
        binding.recyclerViewWithdraws.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewWithdraws.adapter = adapter

        // Collect user cache
        lifecycleScope.launch {
            userViewModel.usersWithAccounts.collect {
                cachedUserList = it
            }
        }

        // Show loader until first withdraw list arrives
        showLoading()
        lifecycleScope.launch {
            viewModel.withdrawsWithNames.collect { list ->
                // Store full list and initialize filtered list
                allWithdrawals = list
                filteredWithdrawals = list
                adapter.update(filteredWithdrawals)
                if (!isDataLoaded) {
                    isDataLoaded = true
                    hideLoading()
                }
            }
        }

        // Trigger data load
        viewModel.refreshData()

        // Wire up the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText.orEmpty().trim()
                filteredWithdrawals = if (q.isEmpty()) {
                    allWithdrawals
                } else {
                    allWithdrawals.filter {
                        it.userName.contains(q, true) ||
                                it.withdraw.transactionId.contains(q, true) ||
                                it.withdraw.address.contains(q, true)
                    }
                }
                adapter.update(filteredWithdrawals)
                return true
            }
        })
    }

    override fun onConfirm(withdraw: WithdrawWithUserName) {
        val txId = withdraw.withdraw.transactionId
        val amount = withdraw.withdraw.amount

        val matchedUser = cachedUserList.find { it.userId?.trim() == withdraw.withdraw.userId.trim() }
        deviceToken = matchedUser?.deviceToken.orEmpty()
        Log.d("DeviceToken", deviceToken)

        firestore.collection("withdraw_requests").document(txId)
            .update("status", "approved")
            .addOnSuccessListener {
                sendNotification(
                    deviceToken,
                    "Your request for withdraw of $$amount is Approved",
                    "approved"
                )
                Toast.makeText(requireContext(), "Withdrawal approved", Toast.LENGTH_SHORT).show()
                viewModel.refreshData()  // refresh list to reflect change
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to approve: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onReject(withdraw: WithdrawWithUserName) {
        val txId = withdraw.withdraw.transactionId
        val amount = withdraw.withdraw.amount

        val matchedUser = cachedUserList.find { it.userId?.trim() == withdraw.withdraw.userId.trim() }
        deviceToken = matchedUser?.deviceToken.orEmpty()
        Log.d("DeviceToken", deviceToken)

        firestore.collection("withdraw_requests").document(txId)
            .update("status", "rejected")
            .addOnSuccessListener {
                sendNotification(
                    deviceToken,
                    "Your request for withdraw of $$amount is Rejected",
                    "rejected"
                )
                Toast.makeText(requireContext(), "Withdrawal rejected", Toast.LENGTH_SHORT).show()
                viewModel.refreshData()  // refresh list to reflect change
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to reject: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBlock(withdraw: WithdrawWithUserName) {
        // unchanged
    }

    override fun onCopy(withdraw: WithdrawWithUserName) {
        val clipboard = requireContext()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Wallet Address", withdraw.withdraw.address)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Wallet address copied", Toast.LENGTH_SHORT).show()
    }

    override fun onUserClick(withdraw: WithdrawWithUserName) {
        val rawUserId = withdraw.withdraw.userId.trim()
        val matched = cachedUserList.firstOrNull { it.userId.trim() == rawUserId }
        if (matched != null) {
            val bundle = bundleOf(
                "userId"                    to matched.userId,
                "name"                      to matched.name,
                "email"                     to matched.email,
                "password"                  to matched.password,
                "phone"                     to matched.phone,
                "referalCode"               to matched.referalCode,
                "accountId"                 to matched.accountId,
                "totalDeposit"              to matched.totalDeposit,
                "currentBalance"            to matched.currentBalance,
                "withdraw"                  to matched.withdraw,
                "totalEarned"               to matched.totalEarned,
                "lifetime_referral_income"  to matched.lifetime_referral_income,
                "lifetime_roi_income"       to matched.lifetime_roi_income,
                "lifetime_team_income"      to matched.lifetime_team_income
            )
            findNavController().navigate(R.id.userProfileFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "No matching user for '$rawUserId'", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotification(deviceToken: String, notification: String, type: String) {
        AccessToken.getAccessTokenAsync(object : AccessToken.AccessTokenCallback {
            override fun onAccessTokenReceived(token: String?) {
                token?.let {
                    Fcm().sendFCMNotification(
                        deviceToken,
                        "Admin BitBloom",
                        "$notification!",
                        type,
                        it
                    )
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
