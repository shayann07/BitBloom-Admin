package com.example.bitbloomadmin.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.example.bitbloomadmin.models.UserModel
import com.example.bitbloomadmin.notifications.AccessToken
import com.example.bitbloomadmin.notifications.Fcm
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AnnoucementFragment : BaseFragment() {

    private var _binding: FragmentAnnoucementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel
    private lateinit var announcementAdapter: AnnouncementAdapter
    private lateinit var usersList : MutableList<UserModel>
    private var firestore = FirebaseFirestore.getInstance()

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

        usersList = ArrayList()
        getAllUsers()
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
                        sendNotification()
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
    private fun sendNotification() {
        AccessToken.getAccessTokenAsync(object : AccessToken.AccessTokenCallback {
            override fun onAccessTokenReceived(token: String?) {
                if (token != null) {
                    val fcm = Fcm()
                    for (user in usersList){
                        Log.d("Notifications", "showNotification: ${user.id}")
                        fcm.sendFCMNotification(
                            user.deviceToken!!,
                            "BitBloom Admin",
                            "New Announcement Alert!",
                            "notification",
                            token
                        )
                    }
                }
            }
        })

    }
    private fun getAllUsers(){
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(UserModel::class.java)
                    usersList.add(user)

                        Log.d("Users", "getAllUsers: ${usersList.size}")

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
