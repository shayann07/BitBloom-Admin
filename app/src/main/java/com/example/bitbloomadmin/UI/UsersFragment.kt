package com.example.bitbloomadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitbloom.bitbloomadmin.adapter.UserListAdapter
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.databinding.FragmentUsersBinding
import com.example.bitbloomadmin.models.UserWithAccount
import kotlinx.coroutines.launch

class UsersFragment : BaseFragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserListAdapter
    private lateinit var viewModel: UserViewModel
    private var allUsers: List<UserWithAccount> = emptyList()
    private var filteredUsers: List<UserWithAccount> = emptyList()

    // flag to end loading only once
    private var isUsersLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        // ViewModel setup (no automatic sync in init)
        val dao        = AppDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(FirebaseHelper(requireContext()), dao)
        val factory    = UserViewModelFactory(repository)
        viewModel      = ViewModelProvider(this, factory)[UserViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        adapter = UserListAdapter(emptyList(), object : UserListAdapter.ClickHandler {
            override fun onClick(user: UserWithAccount) {
                val bundle = bundleOf(
                    "userId"                  to user.userId,
                    "name"                    to user.name,
                    "email"                   to user.email,
                    "password"                to user.password,
                    "phone"                   to user.phone,
                    "referalCode"             to user.referalCode,
                    "accountId"               to user.accountId,
                    "totalDeposit"            to user.totalDeposit,
                    "currentBalance"          to user.currentBalance,
                    "withdraw"                to user.withdraw,
                    "totalEarned"             to user.totalEarned,
                    "lifetime_referral_income" to user.lifetime_referral_income,
                    "lifetime_roi_income"     to user.lifetime_roi_income,
                    "lifetime_team_income"    to user.lifetime_team_income
                )
                findNavController().navigate(R.id.userProfileFragment, bundle)
            }
            override fun onBlock(user: UserWithAccount) {
                // Optional: block logic
            }
        })

        binding.recyclerUserList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUserList.adapter = adapter

        // 1) Show loader until we see at least one Room emission
        showLoading()
        lifecycleScope.launch {
            viewModel.usersWithAccounts.collect { userList ->
                allUsers = userList
                filteredUsers = userList
                adapter.updateData(filteredUsers)

                if (!isUsersLoaded && userList.isNotEmpty()) {
                    isUsersLoaded = true
                    hideLoading()
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText.orEmpty().trim()
                filteredUsers = if (q.isEmpty()) {
                    allUsers
                } else {
                    allUsers.filter {
                        it.name.contains(q, ignoreCase = true) ||
                                it.email.contains(q, ignoreCase = true) ||
                                it.phone.contains(q, ignoreCase = true) || it.userId.contains(q, ignoreCase = true)
                    }
                }
                adapter.updateData(filteredUsers)
                return true
            }
        })


        // 2) Trigger Firestore sync in the background:
        //    because `syncFromFirebase()` now upserts, Room already has data from previous runs.
        viewModel.syncNow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
