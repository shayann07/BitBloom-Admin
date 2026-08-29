package com.example.bitbloomadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.databinding.FragmentUserProfileBinding

class UserProfileFragment : BaseFragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerTrigger(view)

        // Show loader until binding is complete
        showLoading()

        val args = requireArguments()
        binding.apply {
            uid.text               = args.getString("userId", "")
            tvName.text            = args.getString("name", "")
            tvEmail.text           = args.getString("email", "")
            tvPhone.text           = args.getString("phone", "")
            tvDeposit.text         = String.format("%.2f", args.getDouble("totalDeposit", 0.0))
            tvProfit.text          = String.format("%.2f", args.getDouble("totalEarned", 0.0))
            tvReferral.text        = args.getString("referalCode", "N/A")
            tvPassword.text        = "••••••"
            tvLifetimeReferral.text= String.format("%.2f", args.getDouble("lifetime_referral_income", 0.0))
            tvLifetimeRoi.text     = String.format("%.2f", args.getDouble("lifetime_roi_income", 0.0))
            tvLifetimeTeam.text    = String.format("%.2f", args.getDouble("lifetime_team_income", 0.0))
        }

        hideLoading()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
