package com.shino72.location.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shino72.location.adapter.PlanRecyclerviewAdapter
import com.shino72.location.data.Plan
import com.shino72.location.databinding.FragmentPlanBinding
import com.shino72.location.viewmodel.ListViewModel
import com.shino72.location.viewmodel.PlanViewModel
import kotlinx.coroutines.launch

class PlanFragment : Fragment() {

    private var _binding : FragmentPlanBinding? = null
    private val listViewModel : ListViewModel by viewModels()
    private val planViewModel : PlanViewModel by activityViewModels()
    private val binding get() = _binding!!

    private lateinit var adapter : PlanRecyclerviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        val view = binding.root

        initPlanRecyclerView()

        lifecycleScope.launch{
            planViewModel.dbEvent.collect {
                if(it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                }
                if(it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE

                }
                val data = mutableListOf<Plan>()
                it.db?.let {
                    binding.progress.visibility = View.GONE
                    val dt = it.let {plan ->
                        plan.forEach { p->
                           data.add(Plan(
                                name = p.contents,
                                time = "${p.year}-${p.month}-${p.dayOfMonth}",
                                location = p.place ?: ""
                            )
                           )
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
        adapter = PlanRecyclerviewAdapter()
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