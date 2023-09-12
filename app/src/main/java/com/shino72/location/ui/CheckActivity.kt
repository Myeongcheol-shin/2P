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
import com.shino72.location.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class CheckActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCheckBinding
    private val planViewModel : PlanViewModel by viewModels()
    private val locationViewModel : LocationViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check)

        val receiveData = intent.getSerializableExtra("detail") as Plan

        // 툴바 설정.
        binding.toolBar.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_backbtn)
        }

        binding.errorBtn.setOnClickListener {
            requestPermission {
                CoroutineScope(Dispatchers.IO).launch {
                    locationViewModel.getLocation()
                }
            }
        }

        lifecycle.coroutineScope.launchWhenCreated {
            locationViewModel.location.collect {
                if(it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                    binding.errorGroup.visibility = View.GONE
                    binding.endBtn.visibility = View.VISIBLE
                    binding.sucGroup.visibility = View.GONE
                }
                if(it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE
                    binding.errorGroup.visibility = View.VISIBLE
                    binding.endBtn.visibility = View.VISIBLE

                    binding.errorTv2.text = "위치를 다시 검색해주세요!"
                }
                it.data?.let {location ->
                    locationViewModel.distanceInKilometerByHaversine(receiveData.y.toDouble(), receiveData.x.toDouble(), location.latitude, location.longitude)
                }
            }
        }

        locationViewModel.distance.observe(this) {
            binding.progress.visibility = View.INVISIBLE
            // 100m 밖의,
            if(it >= 0.1) {
                binding.errorGroup.visibility = View.VISIBLE
                binding.endBtn.visibility = View.VISIBLE

                binding.errorTv2.text = "실제 거리와 ${df.format(it)}km가 떨어져 있어요."
            }
            // 안에 있다면,
            else {
                binding.endBtn.visibility = View.GONE
                binding.sucGroup.visibility = View.VISIBLE
            }
        }

        binding.endBtn.setOnClickListener {
            finish()
        }

        binding.sucBtn.setOnClickListener {
            receiveData.status = "완료"
            planViewModel.updatePlan(receiveData)
            finish()
        }
    }

    init {
        // 시작할 때 권한 체크 후 현재 위치 측정
        requestPermission {
            CoroutineScope(Dispatchers.IO).launch {
                locationViewModel.getLocation()
            }
        }
    }

    // ted permission : 권한 체크
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

    companion object {
        val df : DecimalFormat = DecimalFormat("#.####")
    }
}