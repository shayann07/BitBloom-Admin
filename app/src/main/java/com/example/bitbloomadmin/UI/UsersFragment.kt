package com.example.bitbloomadmin.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserListAdapter
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val appContext = requireContext().applicationContext
        val dao = AppDatabase.getDatabase(appContext).userDao()
        val repository = UserRepository(FirebaseHelper(requireContext()), dao)
        val factory = UserViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter(emptyList(), object : UserListAdapter.ClickHandler {
            override fun onClick(userAccountItem: UserWithAccount) {
                val bundle = Bundle().apply {
                    putString("userId", userAccountItem.userId)
                }
                findNavController().navigate(R.id.userProfileFragment, bundle)
            }

            override fun onBlock(userAccountItem: UserWithAccount) {
                // Optional: block logic
            }
        })

        binding.recyclerUserList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUserList.adapter = adapter

        // Collect data from ViewModel
        lifecycleScope.launch {
            viewModel.usersWithAccounts.collect { userList ->
                adapter.updateData(userList)
            }
        }
        viewModel.syncNow()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}