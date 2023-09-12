package com.shino72.location.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shino72.location.databinding.PlanListBinding
import com.shino72.location.db.Entity.Plan
import com.shino72.location.ui.DetailActivity

class PlanRecyclerviewAdapter(val context : Context) : RecyclerView.Adapter<PlanRecyclerviewAdapter.PlanViewHolder>() {
    var dataList = mutableListOf<Plan>()
    override fun onBindViewHolder(holder: PlanRecyclerviewAdapter.PlanViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
    inner class PlanViewHolder(private val binding :  PlanListBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(plan : Plan){
            binding.locaitonTv.text= plan.place
            binding.timeTv.text= "${plan.year}-${plan.month}-${plan.dayOfMonth} / ${plan.hour}:${getMinutes(plan.minute)}"
            binding.nameTv.text= plan.contents
            binding.sucTv.text = plan.status
            binding.detailBtn.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("detail",plan)
                context.startActivity(intent)
            }
        }

    }

    private fun getMinutes(m : String) : String = if(m.length == 1) "0$m" else m

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = PlanListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlanViewHolder(binding)
    }

    override fun getItemCount() = dataList.size

}