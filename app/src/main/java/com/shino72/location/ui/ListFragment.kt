package com.shino72.location.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.location.R
import com.shino72.location.databinding.ActivityMainBinding
import com.shino72.location.databinding.FragmentListBinding
import com.shino72.location.viewmodel.ListViewModel
import com.shino72.location.viewmodel.LocationViewModel
import com.shino72.location.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private lateinit var planViewModel: PlanViewModel
    private lateinit var listViewModel: ListViewModel

    private val binding get() = _binding!!


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        planViewModel = ViewModelProvider(this)[PlanViewModel::class.java]
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]


        listViewModel.date.observe(requireActivity()) {
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

                }
            }
        }



        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}