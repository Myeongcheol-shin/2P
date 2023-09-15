package com.shino72.location.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import com.shino72.location.R
import com.shino72.location.databinding.FragmentCalendarBinding
import net.daum.mf.map.api.MapView

class CalendarFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}