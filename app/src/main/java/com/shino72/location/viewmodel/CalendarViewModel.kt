package com.shino72.location.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shino72.location.repository.RoomRepository
import com.shino72.location.utils.DBState
import com.shino72.location.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
@Inject
constructor(
    private val roomRepository: RoomRepository
) : ViewModel()
{
    private var _dbEvent = MutableStateFlow(DBState())

    private var _status = MutableLiveData<Int>(0)
    val status = _status

    val dbEvent : StateFlow<DBState> = _dbEvent

    fun getDB() {
        _dbEvent.value = DBState()
        roomRepository.getAllPlans().onEach {
            when(it)
            {
                is Status.Loading -> {
                    _dbEvent.value = DBState(isLoading = true)
                }
                is Status.Error -> {
                    _dbEvent.value = DBState(error = it.message ?: "")
                }
                is Status.Success -> {
                    _dbEvent.value = DBState(db = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNotFinishedDB() {
        _dbEvent.value = DBState()
        roomRepository.getNotCompletedPlans().onEach {
            when(it)
            {
                is Status.Loading -> {
                    _dbEvent.value = DBState(isLoading = true)
                }
                is Status.Error -> {
                    _dbEvent.value = DBState(error = it.message ?: "")
                }
                is Status.Success -> {
                    _dbEvent.value = DBState(db = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getFinishedDB() {
        _dbEvent.value = DBState()
        roomRepository.getFinishedPlans().onEach {
            when(it)
            {
                is Status.Loading -> {
                    _dbEvent.value = DBState(isLoading = true)
                }
                is Status.Error -> {
                    _dbEvent.value = DBState(error = it.message ?: "")
                }
                is Status.Success -> {
                    _dbEvent.value = DBState(db = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setStatus(s : Int) {
        _status.value = s
    }

}