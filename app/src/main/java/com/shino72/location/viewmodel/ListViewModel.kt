package com.shino72.location.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shino72.location.data.Date
import java.util.Calendar

class ListViewModel : ViewModel() {

    private val _date = MutableLiveData<Date>()
    val date : LiveData<Date> = _date

    private fun getDate() {
        val calendar = Calendar.getInstance()

        // 년 월 구하기
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        val arWeek = arrayOf("월","화","수","목","금","토","일")

        // 한 주의 시작을 월요일로 지정
        calendar.firstDayOfWeek = Calendar.MONDAY
        // 시작일과 특정날짜의 차이
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK] - calendar.firstDayOfWeek
        // 해당 주차의 첫째날을 지정
        calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek)

        val dt = calendar.get(Calendar.DAY_OF_MONTH)

        val daysOfWeek = mutableListOf<Pair<String,String>>()

        for(i in 0 until 7) {
            daysOfWeek.add(Pair(arWeek[i], (dt + i).toString()))
        }
        val date = Date(
            year = year,
            month = month,
            daysOfWeek = daysOfWeek
        )

        _date.value = date

    }
    init {
        getDate()
    }
}