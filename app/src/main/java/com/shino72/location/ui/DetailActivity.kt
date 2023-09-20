package com.shino72.location.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.input.input
import com.shino72.location.MainActivity
import com.shino72.location.R
import com.shino72.location.data.Location
import com.shino72.location.databinding.ActivityDetailBinding
import com.shino72.location.db.Entity.Plan
import com.shino72.location.receiver.AlarmReceiver
import com.shino72.location.utils.DateTimeUtils
import com.shino72.location.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private val planViewModel : PlanViewModel by viewModels()
    private lateinit var receiveData : Plan
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        // 데이터 받기
        receiveData = intent.getSerializableExtra("detail") as Plan
        binding.dateTv.text = "${receiveData.month}월 ${receiveData.dayOfMonth}일 일정"
        binding.contentsTv.text = receiveData.contents
        binding.placeTv.text =receiveData.place
        binding.statusTv.text = receiveData.status
        binding.timeTv.text = "${receiveData.hour}:${getMinutes(receiveData.minute)}"

        if(receiveData.status == "완료") {
            binding.sucBtn.visibility = View.GONE
            binding.statusTv.background = resources.getDrawable(R.drawable.text_background_stroke_red)
            binding.statusTv.setTextColor(resources.getColor(R.color.red))

            //수정 버튼 숨기기
            binding.contentBtn.visibility = View.GONE
            binding.placeBtn.visibility = View.GONE
            binding.timeBtn.visibility = View.GONE
        }


        binding.toolBar.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_backbtn)
        }

        // 완료하기 버튼
        binding.sucBtn.setOnClickListener {
            // 현재 시간과 분이 +- 30분 사이가 아니면 완료가 불가능하도록 설정
            if(isWithin30Minutes(receiveData)) {
                val intent = Intent(applicationContext, CheckActivity::class.java)
                intent.putExtra("detail",receiveData)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "±30분 범위 밖이기에 완료할 수 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        // 장소의 결과로 받아온 데이터 처리
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if(it.resultCode == 9001) {
                val intent = it.data
                val contents = intent?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(getString(R.string.search_key), Location::class.java)
                    } else {
                        intent.getParcelableExtra<Location>(getString(R.string.search_key))
                    }
                }
                if(contents!!.status)
                {
                    receiveData.let {plan ->
                        plan.x = contents.x.toString()
                        plan.y = contents.y.toString()
                        plan.place = contents.placeName
                    }
                    planViewModel.updatePlan(receiveData)

                    binding.placeTv.text = contents.placeName
                }
            }})

        // 장소 수정
        binding.placeBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(getString(R.string.search_key), "")
            activityResultLauncher.launch(intent)
        }


        // 컨텐츠 수정
        binding.contentBtn.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "장소 수정")

                // 수정 완료 버튼 클릭 시
                input(hint = "내용을 입력해주세요.") { dl, text ->
                    binding.contentsTv.text = text

                    receiveData.contents = text.toString()
                    planViewModel.updatePlan(receiveData)
                }

                positiveButton(R.string.submit)
                negativeButton(R.string.negative)
            }
        }

        // 날짜 수정
        binding.timeBtn.setOnClickListener {
            MaterialDialog(this).show {
                dateTimePicker { dialog, datetime ->
                    receiveData.let {
                        it.year = datetime.year.toString()
                        it.month = (datetime.month + 1).toString()
                        it.dayOfMonth = datetime.dayOfMonth.toString()
                        it.hour = datetime.get(Calendar.HOUR_OF_DAY).toString()
                        it.minute = getMinutes(datetime.get(Calendar.MINUTE).toString())

                        binding.dateTv.text = "${it.month}월 ${it.dayOfMonth}일 일정"
                        binding.timeTv.text = "${it.hour}:${it.minute}"

                        it.timestamp = DateTimeUtils.dateTimeToMilliseconds(datetime.year, (datetime.month + 1), datetime.dayOfMonth, datetime.get(Calendar.HOUR_OF_DAY), datetime.get(Calendar.MINUTE))

                    }
                    planViewModel.updatePlan(receiveData)
                }

                positiveButton(R.string.submit)
                negativeButton(R.string.negative)
            }
        }

        // 삭제 버튼
        binding.deleteBtn.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "삭제하기")
                message(text = "정말로 삭제하시겠습니까?")
                positiveButton(text = "삭제"){
                    // 알람 해제
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    val intent = Intent(applicationContext, AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext, receiveData.timestamp.toInt(), intent, PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.cancel(pendingIntent)

                    // db에서 삭제
                    planViewModel.deletePlan(receiveData)
                    finish()
                }
                negativeButton(text = "취소")
            }

        }


    }


    private fun isWithin30Minutes(date : Plan): Boolean {
        // 시간 가져오기.
        val currentDateTime = LocalDateTime.now()
        // 주어진 년, 월, 일, 시간, 분으로 LocalDateTime 생성.
        val targetDateTime = LocalDateTime.of(date.year.toInt(), date.month.toInt(), date.dayOfMonth.toInt(), date.hour.toInt(), date.minute.toInt())
        // 현재 시간과 주어진 시간 간의 분 차이 계산
        val minutesDifference = ChronoUnit.MINUTES.between(currentDateTime, targetDateTime)
        // 시간 차이가 +-30분 사이에 있는지 확인
        return minutesDifference in -30..30
    }

    private fun getMinutes(m : String) : String = if(m.length == 1) "0$m" else m

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home  -> {
                finish()
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}