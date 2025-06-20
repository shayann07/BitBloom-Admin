package com.example.bitbloomadmin.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Viewmodel.AdminSupportVMFactory
import com.example.bitbloomadmin.Viewmodel.AdminSupportViewModel
import com.example.bitbloomadmin.databinding.FragmentSupportBinding
import com.example.bitbloomadmin.ui.BaseFragment

class SupportFragment : BaseFragment() {

    private var _b: FragmentSupportBinding? = null
    private val b get() = _b!!

    private val vm: AdminSupportViewModel by viewModels { AdminSupportVMFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentSupportBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)      // <- keep base call
        setupDrawerTrigger(view)

        with(b) {
            pendingCard.setOnClickListener(nav(R.id.pendingTicketsFragment))
            closedCard.setOnClickListener(nav(R.id.closedTicketsFragment))
            answeredCard.setOnClickListener(nav(R.id.answeredTicketsFragment))
            allCard.setOnClickListener(nav(R.id.allTicketsFragment))

            lifecycleScope.launchWhenStarted {
                vm.pendingCount.collect { pendingAmount.text = it.toString() }
            }
            lifecycleScope.launchWhenStarted {
                vm.closedCount.collect { closedAmount.text = it.toString() }
            }
            lifecycleScope.launchWhenStarted {
                vm.answeredCount.collect { answeredAmount.text = it.toString() }
            }
            lifecycleScope.launchWhenStarted {
                vm.all.collect { allAmount.text = it.size.toString() }
            }
        }
    }

    private fun nav(@IdRes dest: Int) = View.OnClickListener {
        findNavController().navigate(dest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}