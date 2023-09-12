package com.shino72.location.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
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
import com.shino72.location.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
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

        binding.toolBar.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_backbtn)
        }

        // 완료하기 버튼
        binding.sucBtn.setOnClickListener {
            val intent = Intent(applicationContext, CheckActivity::class.java)
            intent.putExtra("detail",receiveData)
            startActivity(intent)
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
                    planViewModel.deletePlan(receiveData)
                    finish()
                }
                negativeButton(text = "취소")
            }

        }


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