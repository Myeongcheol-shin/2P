package com.shino72.location.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.shino72.location.R
import com.shino72.location.databinding.FragmentListBinding
import com.shino72.location.viewmodel.ListViewModel
import com.shino72.location.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val planViewModel: PlanViewModel by activityViewModels()
    private val listViewModel : ListViewModel by viewModels()
    private val binding get() = _binding!!

    private var selectedView = 0

    private lateinit var _innerList : MutableList<LinearLayout>
    private val innerList get() = _innerList

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)


        _innerList = mutableListOf(
            binding.innerLl1,
            binding.innerLl2,
            binding.innerLl3,
            binding.innerLl4,
            binding.innerLl5,
            binding.innerLl6,
            binding.innerLl7,
        )

        val currentDate = Date()

        // SimpleDateFormat을 사용하여 원하는 형식으로 포맷팅
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)



        listViewModel.date.observe(requireActivity()) {
            changeDateBg(it.today)
            binding.date.text = "${it.year}년 ${it.month}월"
            binding.date1Tv.text = it.daysOfWeek[0].second
            binding.date2Tv.text = it.daysOfWeek[1].second
            binding.date3Tv.text = it.daysOfWeek[2].second
            binding.date4Tv.text = it.daysOfWeek[3].second
            binding.date5Tv.text = it.daysOfWeek[4].second
            binding.date6Tv.text = it.daysOfWeek[5].second
            binding.date7Tv.text = it.daysOfWeek[6].second
        }


        lifecycle.coroutineScope.launchWhenCreated {

            planViewModel.dbEvent.collect {
                if(it.isLoading) {

                }
                if(it.error.isNotBlank()) {

                }
                it.db?.let {
                    replaceFragment(PlanFragment.newInstance(formattedDate))
                }
            }
        }


        // linearlayout onClickListener
        binding.apply {
            for(i in 0..6){
                innerList[i].setOnClickListener { changeInnerFragment(i+1) }
            }
        }



        // Inflate the layout for this fragment
        return binding.root
    }
    private fun replaceFragment(f : Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fr, f)
        fragmentTransaction.commit()
    }

    private fun changeDateBg(v : Int) {
        innerList[selectedView].background = null
        innerList[v].setBackgroundResource(R.drawable.date_circle_background)
        selectedView = v
    }

    private fun changeInnerFragment(v : Int) {
        changeDateBg(v-1)
        replaceFragment(PlanFragment.newInstance("${listViewModel.date.value?.year}-${listViewModel.date.value?.month}-${listViewModel.date.value!!.daysOfWeek[v-1].second}"))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}