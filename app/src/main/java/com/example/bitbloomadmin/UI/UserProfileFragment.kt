package com.example.bitbloomadmin.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bitbloom.bitbloomadmin.utils.Utils
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.databinding.FragmentUserProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userId: String
    private lateinit var utils: Utils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        utils = Utils(requireContext())
        utils.startLoadingAnimation()

        val args = arguments

        val userId = args?.getString("userId") ?: ""
        val name = args?.getString("name") ?: ""
        val email = args?.getString("email") ?: ""
        val phone = args?.getString("phone") ?: ""
        val accountId = args?.getString("accountId") ?: ""
        val totalDeposit = args?.getDouble("totalDeposit") ?: 0.0
        val currentBalance = args?.getDouble("currentBalance") ?: 0.0
        val withdraw = args?.getDouble("withdraw") ?: 0.0
        val totalEarned = args?.getDouble("totalEarned") ?: 0.0
        val referralCode = args?.getString("referalCode") ?: "N/A"
        val password = args?.getString("password") ?: "••••••"

        val lifetimeReferralIncome = args?.getDouble("lifetime_referral_income") ?: 0.0
        val lifetimeRoiIncome = args?.getDouble("lifetime_roi_income") ?: 0.0
        val lifetimeTeamIncome = args?.getDouble("lifetime_team_income") ?: 0.0

        // 🟢 Bind to Views
        binding.apply {
            uid.text = userId
            tvName.text = name
            tvEmail.text = email
            tvPhone.text = phone
            tvDeposit.text = totalDeposit.toString()
            tvProfit.text = totalEarned.toString()
            tvReferral.text = referralCode
            tvPassword.text = password

            tvLifetimeReferral.text = lifetimeReferralIncome.toString()
            tvLifetimeRoi.text = lifetimeRoiIncome.toString()
            tvLifetimeTeam.text = lifetimeTeamIncome.toString()
        }


        // 🟢 Bind to Views
        binding.apply {
        uid.text = userId
        tvName.text = name
        tvEmail.text = email
        tvPhone.text = phone
        tvDeposit.text = totalDeposit.toString()
        tvProfit.text = totalEarned.toString()
        tvReferral.text = referralCode
        tvPassword.text = password
        tvLifetimeReferral.text = lifetimeReferralIncome.toString()
        tvLifetimeRoi.text = lifetimeRoiIncome.toString()
        tvLifetimeTeam.text = lifetimeTeamIncome.toString()
    }
        utils.endLoadingAnimation()
    return binding.root
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
}
