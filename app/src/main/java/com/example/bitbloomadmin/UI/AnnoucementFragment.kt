package com.example.bitbloomadmin.UI

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
import com.bitbloom.bitbloomadmin.utils.Utils
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.adapter.AnnouncementAdapter
import com.example.bitbloomadmin.databinding.DialogAddAnnouncementBinding
import com.example.bitbloomadmin.databinding.FragmentAnnoucementBinding
import com.example.bitbloomadmin.models.AnnouncementModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AnnoucementFragment : BaseFragment() {

    private var _binding: FragmentAnnoucementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel
    private lateinit var utils: Utils

    private lateinit var announcementAdapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnoucementBinding.inflate(inflater, container, false)

        // ViewModel setup
        val appContext = requireContext().applicationContext
        val dao = AppDatabase.getDatabase(appContext).userDao()
        val repository = UserRepository(FirebaseHelper(requireContext()), dao)
        val factory = UserViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        utils = Utils(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // RecyclerView + Adapter
        announcementAdapter = AnnouncementAdapter()
        binding.rvAnnouncements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = announcementAdapter
        }
        // 1) Initial load of announcements
        viewModel.fetchAnnouncements()

        // 2) Observe and submit into adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.announcements.collect { list ->
                announcementAdapter.submitList(list)
            }
        }

        // FAB → Add dialog
        binding.fabAddAnnouncement.setOnClickListener {
            showAddAnnouncementDialog()
        }
    }

    private fun showAddAnnouncementDialog() {
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
                Toast.makeText(requireContext(),
                    "Please fill both title and message",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val announcement = AnnouncementModel(
                announcementTitlte = title,
                message           = message
            )

            // add + refresh
            viewModel.addAnnouncement(announcement)
            viewModel.fetchAnnouncements()

            Toast.makeText(requireContext(),
                "Announcement added",
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
