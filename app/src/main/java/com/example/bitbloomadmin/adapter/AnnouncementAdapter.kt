package com.example.bitbloomadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.models.AnnouncementModel


class AnnouncementAdapter :
    ListAdapter<AnnouncementModel, AnnouncementAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvAnnouncementTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvAnnouncementMessage)

        fun bind(item: AnnouncementModel) {
            tvTitle.text = item.announcementTitlte
            tvMessage.text = item.message
        }
    }
    private class DiffCallback : DiffUtil.ItemCallback<AnnouncementModel>() {
        override fun areItemsTheSame(old: AnnouncementModel, new: AnnouncementModel): Boolean {
            return old.id == new.id
        }
        override fun areContentsTheSame(old: AnnouncementModel, new: AnnouncementModel): Boolean {
            return old == new
        }
    }
}
