package com.shino72.location.ui

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
import com.shino72.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private lateinit var locationViewModel : LocationViewModel

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        binding.apply {
            btn.setOnClickListener {
                requestPermission {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        locationViewModel.getLocation()
                    }
                }
            }
        }
        lifecycle.coroutineScope.launchWhenCreated {
            locationViewModel.location.collect {
                if(it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                    binding.text.text = "Loading..."
                }
                if(it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE
                    binding.text.text = "${it.error}"
                }
                it.data?.let {
                    binding.progress.visibility = View.GONE
                    binding.text.text = "Success : latitude => ${it.latitude} / longitude ${it.longitude}"
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

    private fun requestPermission(logic : () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(requireContext(), "권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                }
            }).setDeniedMessage("위치 권한을 허용해주세요.").setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION).check()
    }
}