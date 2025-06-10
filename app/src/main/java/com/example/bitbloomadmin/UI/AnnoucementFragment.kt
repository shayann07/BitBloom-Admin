package com.example.bitbloomadmin.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.adapter.AnnouncementAdapter
import com.example.bitbloomadmin.databinding.DialogAddAnnouncementBinding
import com.example.bitbloomadmin.databinding.FragmentAnnoucementBinding
import com.example.bitbloomadmin.models.AnnouncementModel
import kotlinx.coroutines.launch

class AnnoucementFragment : BaseFragment() {

    private var _binding: FragmentAnnoucementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel
    private lateinit var announcementAdapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnoucementBinding.inflate(inflater, container, false)

        val dao        = AppDatabase.getDatabase(requireContext()).userDao()
        val repo       = UserRepository(FirebaseHelper(requireContext()), dao)
        val factory    = UserViewModelFactory(repo)
        viewModel      = ViewModelProvider(this, factory)[UserViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // RecyclerView + Adapter
        announcementAdapter = AnnouncementAdapter()
        binding.rvAnnouncements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter       = announcementAdapter
        }

        // Initial fetch with loader
        viewLifecycleOwner.lifecycleScope.launch {
            showLoading()
            try {
                viewModel.fetchAnnouncements()
            } finally {
                hideLoading()
            }
        }

        // Observe and submit into adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.announcements.collect { list ->
                announcementAdapter.submitList(list)
            }
        }

        // FAB → Add dialog (no change needed here)
        binding.fabAddAnnouncement.setOnClickListener {
            val dialogBinding = DialogAddAnnouncementBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create().apply {
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

            dialogBinding.btnAddAnnouncement.setOnClickListener {
                val title   = dialogBinding.etAnnouncementHeading.text.toString().trim()
                val message = dialogBinding.etAnnouncementMessage.text.toString().trim()
                if (title.isEmpty() || message.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill both title and message", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val announcement = AnnouncementModel(
                    announcementTitlte = title,
                    message            = message
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    showLoading()
                    try {
                        viewModel.addAnnouncement(announcement)
                        viewModel.fetchAnnouncements()
                    } finally {
                        hideLoading()
                    }
                    Toast.makeText(requireContext(), "Announcement added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
