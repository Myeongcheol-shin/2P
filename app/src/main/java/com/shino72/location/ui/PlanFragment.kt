package com.shino72.location.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shino72.location.adapter.PlanRecyclerviewAdapter
import com.shino72.location.databinding.FragmentPlanBinding
import com.shino72.location.db.Entity.Plan
import com.shino72.location.viewmodel.PlanViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlanFragment : Fragment() {

    private var _binding : FragmentPlanBinding? = null
    private val planViewModel : PlanViewModel by activityViewModels()
    private val binding get() = _binding!!

    private lateinit var adapter : PlanRecyclerviewAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        val view = binding.root


        initPlanRecyclerView()

        lifecycleScope.launch{
            planViewModel.dbEvent.collectLatest {
                if(it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                }
                if(it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE
                }
                val arg = arguments!!.getString(DATE_CODE)!!.split("-")
                val year = arg[0]
                val month = arg[1]
                val day = arg[2]

                val data = mutableListOf<Plan>()
                it.db?.let {
                    binding.progress.visibility = View.GONE
                    it.let {plan ->
                        plan.forEach { p->
                            if(year == p.year && month == p.month && p.dayOfMonth == day) {
                                data.add(p)
                            }
                        }
                    }
                }
                adapter.dataList = data
                adapter.notifyDataSetChanged()
            }
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun initPlanRecyclerView(){
        adapter = PlanRecyclerviewAdapter(requireContext())
        binding.rc.adapter=adapter
        binding.rc.layoutManager= LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val DATE_CODE = "data"

        fun newInstance(date: String): PlanFragment {
            val fragment = PlanFragment()
            val args = Bundle()
            args.putString(DATE_CODE, date)
            fragment.arguments = args
            return fragment
        }
    }

}