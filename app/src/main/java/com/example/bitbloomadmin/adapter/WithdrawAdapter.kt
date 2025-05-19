package com.example.bitbloomadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bitbloom.bitbloomadmin.adapter.UserListAdapter
import com.bitbloom.bitbloomadmin.adapter.UserListAdapter.ClickHandler
import com.example.bitbloomadmin.databinding.ItemAllWithdrawalsRequestsBinding
import com.example.bitbloomadmin.models.UserWithAccount
import com.example.bitbloomadmin.models.WithdrawModel
import com.example.bitbloomadmin.models.WithdrawWithUserName

class WithdrawAdapter( private var withdrawList: List<WithdrawWithUserName> = listOf(),
                       private val handler: WithdrawHandler
) : RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder>() {


    inner class WithdrawViewHolder(val binding: ItemAllWithdrawalsRequestsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WithdrawWithUserName) {
            val initials = item.userName.split(" ")
                .mapNotNull { it.firstOrNull()?.toString()?.uppercase() }
                .joinToString("").take(2).ifEmpty { "NA" }

            binding.avatar.text = initials
            binding.name.text = item.userName
            binding.amount.text = "Rs. ${item.withdraw.amount}"
            binding.walletAddress.text = item.withdraw.address
//            binding.status.text = item.withdraw.status.replaceFirstChar { it.uppercase() }

            binding.btnConfirm.setOnClickListener {
                handler.onConfirm(item)
            }
            binding.btnReject.setOnClickListener {
                handler.onReject(item)
            }
            binding.checkboxBlock.setOnClickListener{
                handler.onBlock(item)
            }
            binding.copyContainer.setOnClickListener {
                handler.onCopy(item)
            }

            binding.name.setOnClickListener {
                handler.onUserClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAllWithdrawalsRequestsBinding.inflate(inflater, parent, false)
        return WithdrawViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WithdrawViewHolder, position: Int) {
        holder.bind(withdrawList[position])
    }

    fun update(newList: List<WithdrawWithUserName>) {
        withdrawList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = withdrawList.size

    interface WithdrawHandler{
        fun onConfirm(withdraw: WithdrawWithUserName)
        fun onReject(withdraw: WithdrawWithUserName)
        fun onBlock(withdraw : WithdrawWithUserName)
        fun onCopy(withdraw: WithdrawWithUserName)
        fun onUserClick(withdraw: WithdrawWithUserName)

    }
}