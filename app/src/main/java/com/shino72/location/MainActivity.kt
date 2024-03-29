package com.shino72.location

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.nikartm.button.FitButton
import com.google.android.material.textfield.TextInputEditText
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.location.data.Location
import com.shino72.location.data.MainPageType
import com.shino72.location.databinding.ActivityMainBinding
import com.shino72.location.db.Entity.Plan
import com.shino72.location.receiver.AlarmReceiver
import com.shino72.location.ui.CalendarFragment
import com.shino72.location.ui.ListFragment
import com.shino72.location.ui.SearchActivity
import com.shino72.location.utils.DateTimeUtils
import com.shino72.location.viewmodel.MainViewModel
import com.shino72.location.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapView: MapView
    private var _binding: ActivityMainBinding? = null
    private val dbViewModel : PlanViewModel by viewModels()
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var dialog: Dialog
    private val binding get() = _binding!!
    private var contents : Location? = null

    // fragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 알람 권한  체크 (티라미수 이상이면 알람 권한 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission()
        }
        // 오버레이 권한
        if (!Settings.canDrawOverlays(this)){
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${packageName}"))
            startActivity(intent)
        }
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dialog = Dialog(this)
        setBottomDialog()

        init()

        binding.bottomNav.background = null

        mainViewModel.currentPageType.observe(this){
            changeFragment(it)
        }
        binding.bottomNav.setOnItemSelectedListener {
            mainViewModel.setCurrentPage(it.itemId)
        }

        dbViewModel.getDB()

        binding.fab.setOnClickListener {
            showDialog()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun setBottomDialog() {
        dialog.setContentView(R.layout.bottom_sheet_layout)

        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)

        // apply button
        val applyButton = dialog.findViewById<AppCompatButton>(R.id.apply_btn)

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

        // time_picker setting
        val timePicker = dialog.findViewById<TimePicker>(R.id.time_picker)
        timePicker.setIs24HourView(true)
        timePicker.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS // Line 제거

        // date_picker setting
        val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
        datePicker.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS // Line 제거

        // text_field
        // focus off
        val editText = dialog.findViewById<TextInputEditText>(R.id.content_et)
        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터 키 또는 다른 작업을 처리할 때 여기에 코드 추가
                editText.clearFocus() // 포커스 해제

                if(editText.text != null && editText.text!!.isEmpty()) {
                    editText.error = "공백이 될 수 없습니다."
                }
                else {
                    editText.error = null
                }

                return@setOnEditorActionListener true
            }
            false
        }

        applyButton.setOnClickListener {
            // 공백 체크
            if(editText.text != null && editText.text!!.isEmpty()) {
                editText.error = "공백이 될 수 없습니다."
                Toast.makeText(this, "내용은 공백이 될 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 현재 시간 가져오기
            val nowTime = System.currentTimeMillis()

            // 날짜 체크
            val calendar = Calendar.getInstance()

            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, timePicker.hour, timePicker.minute,0)
            val contentsMilliSec = calendar.timeInMillis

            if(nowTime > contentsMilliSec) {
                Toast.makeText(this, "현재 시간 이전으로 설정이 불가능합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(contents == null) {
                Toast.makeText(this, "장소를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val timestamp = dateTimeToMilliseconds(datePicker.year, datePicker.month, datePicker.dayOfMonth, timePicker.hour, timePicker.minute)

            val plan = Plan(
                id = 0,
                place = contents!!.placeName,
                x = contents!!.x!!,
                y = contents!!.y!!,
                contents = editText.text.toString(),
                year = datePicker.year.toString(),
                month = (datePicker.month + 1).toString(),
                dayOfMonth = datePicker.dayOfMonth.toString(),
                hour = timePicker.hour.toString(),
                minute = timePicker.minute.toString(),
                timestamp = timestamp
            )

            // 알람 추가
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("detail",plan)
            val pendingIntent = PendingIntent.getBroadcast(
                this, timestamp.toInt() , intent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timestamp,
                pendingIntent
            )

            // db에 추가
            dbViewModel.insertPlan(
                plan
            )

            // 화면 닫기
            dialog.dismiss()

            // 새롭게 업데이트
            dbViewModel.getDB()


        }


        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

    }
    private fun dateTimeToMilliseconds(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)
        return calendar.timeInMillis
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
                contents = intent?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(getString(R.string.search_key), Location::class.java)
                    } else {
                        intent.getParcelableExtra<Location>(getString(R.string.search_key))
                    }
                }
                if(contents!!.status)
                {

                    dialog.findViewById<TextView>(R.id.placeName_tv).text = contents!!.placeName
                    // 중심점 변경
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(contents!!.y!!.toDouble(), contents!!.x!!.toDouble()), true)

                    // 마커
                    val mapPoint = MapPoint.mapPointWithGeoCoord(contents!!.y!!.toDouble(), contents!!.x!!.toDouble())
                    val marker = MapPOIItem()
                    marker.itemName = contents!!.placeName
                    marker.tag = 0
                    marker.mapPoint = mapPoint;
                    marker.markerType = MapPOIItem.MarkerType.BluePin
                    marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    mapView.addPOIItem(marker)

                }
            }
        })

        dbViewModel.getDB()
    }

    // 권한 체크
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(applicationContext, "알람 요청을 위해 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                }
            }).setDeniedMessage("알람 권한을 허용해주세요.").setPermissions(android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.SYSTEM_ALERT_WINDOW
            ).check()
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
    private fun changeFragment(pageType: MainPageType) {
        val transaction = supportFragmentManager.beginTransaction()
        var targetFragment = supportFragmentManager.findFragmentByTag(pageType.tag)

        if (targetFragment == null) {
            targetFragment = getFragment(pageType)
            transaction.add(R.id.fl, targetFragment, pageType.tag)
        }
        transaction.show(targetFragment)
        MainPageType.values()
            .filterNot { it == pageType }
            .forEach { type ->
                supportFragmentManager.findFragmentByTag(type.tag)?.let {
                    transaction.hide(it)
                }
            }
        transaction.commitAllowingStateLoss()
    }

    private fun getFragment(pageType: MainPageType): Fragment {
        var fragment : Fragment = ListFragment()
        when (pageType.title)
        {
            "list" -> fragment = ListFragment()
            "calendar" -> fragment = CalendarFragment()
        }
        return fragment
    }

    companion object {
        const val SEARCH_CODE = 9001
    }

}