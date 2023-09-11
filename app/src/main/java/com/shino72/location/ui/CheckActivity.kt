package com.shino72.location.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.location.R
import com.shino72.location.databinding.ActivityCheckBinding
import com.shino72.location.db.Entity.Plan
import com.shino72.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCheckBinding
    private val locationViewModel : LocationViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check)

        val receiveData = intent.getSerializableExtra("detail") as Plan


        lifecycle.coroutineScope.launchWhenCreated {
            locationViewModel.location.collect {
                if(it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                }
                if(it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE
                    binding.progressTv.text = "Error : ${it.error}"
                }
                it.data?.let {location ->
                    locationViewModel.distanceInKilometerByHaversine(receiveData.y.toDouble(), receiveData.x.toDouble(), location.latitude, location.longitude)
                }
            }
        }

        locationViewModel.distance.observe(this) {
            binding.progress.visibility = View.INVISIBLE
            // 100m 안의 거리면,
            if(it < 0.1) {
                Toast.makeText(this, "100m 안입니다. 거리 : $it", Toast.LENGTH_SHORT).show()
                binding.progressTv.text = "거리 : $it"
            }
            // 밖이면,
            else {
                Toast.makeText(this, "100m 밖입니다. 거리 : $it", Toast.LENGTH_SHORT).show()
                binding.progressTv.text = "거리 : $it"
            }
        }
    }

    init {
        requestPermission {
            CoroutineScope(Dispatchers.IO).launch {
                locationViewModel.getLocation()
            }
        }
    }

    private fun requestPermission(logic : () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(applicationContext, "권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                }
            }).setDeniedMessage("위치 권한을 허용해주세요.").setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION).check()
    }
}