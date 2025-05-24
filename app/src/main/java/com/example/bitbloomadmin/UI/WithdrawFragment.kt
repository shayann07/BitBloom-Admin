package com.example.bitbloomadmin.UI

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Factories.WithdrawViewModelFactory
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Repository.WithdrawRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.Viewmodel.WithdrawViewModel
import com.example.bitbloomadmin.adapter.WithdrawAdapter
import com.example.bitbloomadmin.databinding.FragmentWithdrawBinding
import com.example.bitbloomadmin.models.UserWithAccount
import com.example.bitbloomadmin.models.WithdrawWithUserName
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class WithdrawFragment : BaseFragment(), WithdrawAdapter.WithdrawHandler {

    private lateinit var binding: FragmentWithdrawBinding
    private lateinit var viewModel: WithdrawViewModel
    private lateinit var adapter: WithdrawAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userViewModel: UserViewModel
    private var cachedUserList: List<UserWithAccount> = emptyList()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false)

        val dao = AppDatabase.getDatabase(requireContext()).withdrawDao()
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()

        val helper = FirebaseHelper(requireContext())
        val repository = WithdrawRepository(dao, helper)
        val factory = WithdrawViewModelFactory(repository)
        val userRepository = UserRepository(FirebaseHelper(requireContext()), userDao)

        val userFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]
        view?.let { setupDrawerTrigger(it) }



        firestore = FirebaseFirestore.getInstance()
        viewModel = ViewModelProvider(this, factory)[WithdrawViewModel::class.java]

        adapter = WithdrawAdapter(handler = this)
        binding.recyclerViewWithdraws.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewWithdraws.adapter = adapter
        lifecycleScope.launchWhenStarted {
            userViewModel.usersWithAccounts.collect {
                cachedUserList = it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.withdrawsWithNames.collect { list ->
                adapter.update(list)
            }
        }







        viewModel.refreshData()
        return binding.root
    }

    override fun onConfirm(withdraw: WithdrawWithUserName) {
        val txId = withdraw.withdraw.transactionId
        firestore.collection("withdraw_requests").document(txId)
            .update("status", "approved")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Withdrawal approved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to approve: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onReject(withdraw: WithdrawWithUserName) {
        val txId = withdraw.withdraw.transactionId
        firestore.collection("withdraw_requests").document(txId)
            .update("status", "rejected")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Withdrawal rejected", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to reject: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBlock(withdraw: WithdrawWithUserName) {
        val userId = withdraw.withdraw.userId

        firestore.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDoc = querySnapshot.documents[0]
                    val userRef = userDoc.reference

                    val updates = mapOf(
                        "status" to "blocked",
                        "isBlocked" to true
                    )

                    userRef.update(updates)
                        .addOnSuccessListener {
                            firestore.collection("withdraw_requests")
                                .whereEqualTo("userId", userId)
                                .get()
                                .addOnSuccessListener { txSnapshot ->
                                    val batch = firestore.batch()
                                    for (doc in txSnapshot.documents) {
                                        batch.update(doc.reference, "status", "blocked")
                                    }
                                    batch.commit()
                                        .addOnSuccessListener {
                                            Toast.makeText(requireContext(), "User blocked & all transactions updated", Toast.LENGTH_SHORT).show()
                                        }
                                }
                        }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCopy(withdraw: WithdrawWithUserName) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Wallet Address", withdraw.withdraw.address)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Wallet address copied", Toast.LENGTH_SHORT).show()
    }

    override fun onUserClick(withdraw: WithdrawWithUserName) {
        val rawUserId = withdraw.withdraw.userId
        val userId = rawUserId.trim()

        // ✅ Show toast with userId (even if empty)
        Toast.makeText(requireContext(), "Clicked User ID: '$userId'", Toast.LENGTH_SHORT).show()

        val matchedUser = cachedUserList.firstOrNull { it.userId.trim() == userId }


        if (matchedUser != null) {
            val bundle = bundleOf(
                "userId" to matchedUser.userId,
                "name" to matchedUser.name,
                "email" to matchedUser.email,
                "password" to matchedUser.password,
                "phone" to matchedUser.phone,
                "referalCode" to matchedUser.referalCode,
                "accountId" to matchedUser.accountId,
                "totalDeposit" to matchedUser.totalDeposit,
                "currentBalance" to matchedUser.currentBalance,
                "withdraw" to matchedUser.withdraw,
                "totalEarned" to matchedUser.totalEarned,
                "lifetime_referral_income" to matchedUser.lifetime_referral_income,
                "lifetime_roi_income" to matchedUser.lifetime_roi_income,
                "lifetime_team_income" to matchedUser.lifetime_team_income
            )

            findNavController().navigate(R.id.userProfileFragment, bundle)
        } else {
            Toast.makeText(
                requireContext(),
                "❌ No matching user found for: '$userId'",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    }
