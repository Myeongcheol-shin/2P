package com.shino72.location.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shino72.location.R
import com.shino72.location.data.MainPageType

class MainViewModel : ViewModel() {

    private val _currentPageType = MutableLiveData(MainPageType.PAGE1)
    val currentPageType: LiveData<MainPageType> = _currentPageType

    fun setCurrentPage(menuItemId: Int): Boolean {
        val pageType = getPageType(menuItemId)
        changeCurrentPage(pageType)

        return true
    }

    private fun getPageType(menuItemId: Int): MainPageType {
        return when (menuItemId) {
            R.id.list -> MainPageType.PAGE1
            R.id.calendar -> MainPageType.PAGE2
            else -> MainPageType.PAGE1
        }
    }

    private fun changeCurrentPage(pageType: MainPageType) {
        if (currentPageType.value == pageType) return
        _currentPageType.value = pageType
    }


}