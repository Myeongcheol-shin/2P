package com.shino72.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shino72.location.db.Entity.Plan
import com.shino72.location.repository.RoomRepository
import com.shino72.location.utils.DBEvent
import com.shino72.location.utils.DBState
import com.shino72.location.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel
@Inject
constructor(
    private val planRepository : RoomRepository
) : ViewModel() {
    private var _dbEvent = MutableStateFlow(DBState())

    private val _db =  MutableStateFlow<DBEvent>(DBEvent.LoadDB)


    val dbEvent : StateFlow<DBState> = _dbEvent


    fun getDB() {
        _dbEvent.value = DBState()
        planRepository.getAllPlans().onEach {
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


    fun insertPlan(plan: Plan) {
        viewModelScope.launch(Dispatchers.IO) {
            planRepository.insertPlan(plan)
        }
    }

    fun deletePlan(plan: Plan) {
        viewModelScope.launch(Dispatchers.IO) {
            planRepository.deletePlan(plan)
        }
    }

    fun updatePlan(plan:Plan) {
        viewModelScope.launch(Dispatchers.IO) {
            planRepository.updatePlan(plan)
        }
    }

}