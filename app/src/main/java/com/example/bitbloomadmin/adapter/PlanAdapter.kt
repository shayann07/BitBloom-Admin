package com.example.bitbloomadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.models.PlanModel

class PlanAdapter(
    private var plans: List<PlanModel> = emptyList()
) : RecyclerView.Adapter<PlanAdapter.PlanVH>() {

    inner class PlanVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.tv_plan_name)
        private val min_invested = itemView.findViewById<TextView>(R.id.tv_invested_amount)
        private val roi = itemView.findViewById<TextView>(R.id.tv_daily_roi)
        private val durationDays = itemView.findViewById<TextView>(R.id.tv_duration_days)

        fun bind(p: PlanModel) {
            name.text = p.name
            min_invested.text = "$${p.minInvestment}"
            roi.text = "${p.directProfit}%"
            durationDays.text = p.durationDays.toString()
            // …and any icons or backgrounds…
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlanVH(LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false))

    override fun onBindViewHolder(holder: PlanVH, position: Int) =
        holder.bind(plans[position])

    override fun getItemCount() = plans.size

    fun submitList(new: List<PlanModel>) {
        plans = new
        notifyDataSetChanged()
    }
}
