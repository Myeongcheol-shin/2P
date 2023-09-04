package com.shino72.location.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shino72.location.databinding.FragmentCalendarBinding
import net.daum.mf.map.api.MapView

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        val mapView = MapView(activity)
        val mapViewContainer = binding.mapView as ViewGroup

        mapViewContainer.addView(mapView)

        return binding.root


    }
}