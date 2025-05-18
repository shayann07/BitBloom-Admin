package com.bitbloom.bitbloomadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.models.UserWithAccount

class UserListAdapter(
    private var fullList: List<UserWithAccount>,
    private val clickHandler: ClickHandler
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private var filteredList: List<UserWithAccount> = fullList
    private val blockedUserIds = mutableSetOf<String>()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.userNameTextView)
        val currentBalance: TextView = itemView.findViewById(R.id.currentBalance)
        val withdrawText: TextView = itemView.findViewById(R.id.withdrawTextView)
        val profitText: TextView = itemView.findViewById(R.id.profitTextView)
        val blockCheckBox: CheckBox = itemView.findViewById(R.id.blockCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = filteredList[position]
        holder.nameText.text = item.name
        holder.currentBalance.text = item.currentBalance.toString()
        holder.withdrawText.text = item.withdraw.toString()
        holder.profitText.text = item.totalEarned.toString()

        // 🔘 Click to open user profile
        holder.itemView.setOnClickListener {
            clickHandler.onClick(item)
        }

        // 🔐 Handle block checkbox
        holder.blockCheckBox.setOnCheckedChangeListener(null)
        holder.blockCheckBox.isChecked = blockedUserIds.contains(item.userId)

        holder.blockCheckBox.setOnClickListener {
            if (!blockedUserIds.contains(item.userId)) {
                blockedUserIds.clear()
                blockedUserIds.add(item.userId)
                notifyDataSetChanged()
                clickHandler.onBlock(item)
            }
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateData(newList: List<UserWithAccount>) {
        fullList = newList
        filteredList = newList
        blockedUserIds.clear()
        notifyDataSetChanged()
    }

    fun filterList(
        searchQuery: String,
        selectedStatus: String,
        userIdToStatus: Map<String, String>
    ) {
        filteredList = fullList.filter { user ->
            val matchesSearch = user.name.lowercase().contains(searchQuery)
            val userStatus = userIdToStatus[user.userId] ?: "inactive"
            val matchesStatus = selectedStatus == "all" || userStatus == selectedStatus
            matchesSearch && matchesStatus
        }
        notifyDataSetChanged()
    }

    interface ClickHandler {
        fun onClick(userAccountItem: UserWithAccount)
        fun onBlock(userAccountItem: UserWithAccount)
    }
}