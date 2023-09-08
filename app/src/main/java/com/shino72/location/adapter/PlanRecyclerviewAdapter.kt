package com.shino72.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shino72.location.data.Plan
import com.shino72.location.databinding.PlanListBinding

class PlanRecyclerviewAdapter : RecyclerView.Adapter<PlanRecyclerviewAdapter.PlanViewHolder>() {
    var dataList = mutableListOf<Plan>()
    override fun onBindViewHolder(holder: PlanRecyclerviewAdapter.PlanViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
    inner class PlanViewHolder(private val binding :  PlanListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(plan : Plan){
            binding.locaitonTv.text= plan.location
            binding.timeTv.text= plan.time
            binding.nameTv.text= plan.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = PlanListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlanViewHolder(binding)
    }

    override fun getItemCount() = dataList.size

}