package com.shino72.location.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shino72.location.R
import com.shino72.location.adapter.PlanRecyclerviewAdapter
import com.shino72.location.databinding.FragmentCalendarBinding
import com.shino72.location.db.Entity.Plan
import com.shino72.location.viewmodel.CalendarViewModel
import com.shino72.location.viewmodel.PlanViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapView

class CalendarFragment : Fragment() {

    private lateinit var adapter : PlanRecyclerviewAdapter

    private val calendarViewModel : CalendarViewModel by activityViewModels()
    private val roomViewModel : PlanViewModel by activityViewModels()

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var filterStatus = false

    // animation
    private lateinit var moveLeftAnim : Animation
    private lateinit var moveRightAnim : Animation
    private lateinit var fadeIn : Animation
    private lateinit var fadeOut : Animation

    private var initialX = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        filterStatus = false
        initialX = binding.fab.x

        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recyclerview setting
        initRecyclerView()

        // observe filtering change
        calendarViewModel.status.observe(this) {
            when(it) {
                0 -> {
                    calendarViewModel.getDB()
                }
                1 -> {
                    calendarViewModel.getFinishedDB()
                }
                2 -> {
                    calendarViewModel.getNotFinishedDB()
                }
            }
        }

        // animation set
        moveLeftAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_slide_left)
        moveRightAnim = TranslateAnimation(0f, initialX - binding.fab.x, 0f,0f)
        fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_fade_in)
        fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_fade_out)

        fadeOut.setAnimationListener( object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.filterGroup.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        }
        )

        fadeIn.setAnimationListener( object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.filterGroup.visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        }
        )

        // dbEvent
        lifecycleScope.launch {
            calendarViewModel.dbEvent.collectLatest {
                if (it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                }
                if (it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE
                }
                it.db?.let {
                    binding.progress.visibility = View.GONE
                    adapter.dataList = it as MutableList<Plan>
                    adapter.notifyDataSetChanged()
                }
            }
        }

        lifecycleScope.launch {
            roomViewModel.dbEvent.collectLatest { it ->
                // 데이터가 새롭게 업데이트 된다면.
                it.db?.let {
                    calendarViewModel.status.value?.let { v ->
                        calendarViewModel.setStatus(v)
                    }
                }
            }
        }


        // 눌렀을 때 fab 애니메이션
        binding.fab.setOnClickListener{
            if(filterStatus) {
                binding.filterGroup.startAnimation(fadeOut)
                filterStatus = !filterStatus
                it.startAnimation(moveRightAnim)
            }
            else {
                binding.filterGroup.startAnimation(fadeIn)
                filterStatus = !filterStatus
                it.startAnimation(moveLeftAnim)
            }
        }

        // fab click event
        binding.apply {
            // 최신순 : 0
            filterDate.setOnClickListener {
                calendarViewModel.setStatus(0)
            }
            // 끝 : 1
            filterEnd.setOnClickListener {
                calendarViewModel.setStatus(1)
            }
            // 아직 : 2
            filterYet.setOnClickListener {
                calendarViewModel.setStatus(2)
            }
        }
    }


    private fun initRecyclerView() {
        adapter = PlanRecyclerviewAdapter(requireContext())
        binding.rc.adapter=adapter
        binding.rc.layoutManager= LinearLayoutManager(requireContext())
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        calendarViewModel.getDB()
    }
}