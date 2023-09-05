package com.shino72.location

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.nikartm.button.FitButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.location.databinding.ActivityMainBinding
import com.shino72.location.ui.CalendarFragment
import com.shino72.location.ui.ListFragment
import com.shino72.location.ui.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapView: MapView
    private var _binding: ActivityMainBinding? = null
    private lateinit var dialog: Dialog
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = Dialog(this)
        setBottomDialog()

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        init()


        // bottom navigation view
        replaceFragment(ListFragment())
        binding.bottomNav.background = null
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.list -> {
                    replaceFragment(ListFragment())
                }
                R.id.calendar -> {
                    replaceFragment(CalendarFragment())
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            showDialog()
        }
    }

    private fun setBottomDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout)

        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        try {
            mapView = MapView(this)
            val mapViewContainer = dialog.findViewById<RelativeLayout>(R.id.map_view) as ViewGroup

            mapViewContainer.addView(mapView)
        }
        catch (e : Exception) {
            Log.d("MapView", e.localizedMessage ?: "")
        }

        dialog.findViewById<FitButton>(R.id.search_btn).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(getString(R.string.search_key), "")
            activityResultLauncher.launch(intent)
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.BOTTOM);
    }
    private fun showDialog() {
        try {
            mapView = MapView(this)
            val mapViewContainer = dialog.findViewById<RelativeLayout>(R.id.map_view) as ViewGroup

            mapViewContainer.addView(mapView)
        }
        catch (e : Exception) {
            Log.d("MapView", e.localizedMessage ?: "")
        }

        dialog.show()
    }

    private fun init() {
        //activityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if(it.resultCode == SEARCH_CODE) {
                val intent = it.data
                val contents = intent?.getParcelableExtra<com.shino72.location.utils.data.Location>(getString(R.string.search_key))
                if(contents!!.status)
                {
                    dialog.findViewById<TextView>(R.id.placeName_tv).text = contents.placeName
                    // 중심점 변경
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(contents.y!!.toDouble(), contents.x!!.toDouble()), true)

                    // 마커
                    val mapPoint = MapPoint.mapPointWithGeoCoord(contents.y!!.toDouble(), contents.x!!.toDouble())
                    val marker = MapPOIItem()
                    marker.itemName = contents.placeName
                    marker.tag = 0
                    marker.mapPoint = mapPoint;
                    marker.markerType = MapPOIItem.MarkerType.BluePin
                    marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    mapView.addPOIItem(marker)

                }
            }
        })
    }

    private fun openActivityResultLauncher(): ActivityResultLauncher<Intent> {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "수신 성공", Toast.LENGTH_SHORT).show()
                try {
                    dialog.findViewById<TextView>(R.id.result_tv).text = result.data?.getStringExtra("comeback")
                }
                catch (e : Exception){
                    Log.d("Search_Result : ", e.localizedMessage ?: "")
                }
            }
            else {
                Toast.makeText(this, "수신 실패", Toast.LENGTH_SHORT).show()
            }
        }
        return resultLauncher
    }

    private fun replaceFragment(f : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl, f)
        fragmentTransaction.commit()
    }
    private fun requestPermission(logic : () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@MainActivity, "권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                }
            }).setDeniedMessage("위치 권한을 허용해주세요.").setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION).check()
    }
    companion object {
        const val SEARCH_CODE = 9001
    }
}