package com.shino72.location.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shino72.location.repository.LocationRepository
import com.shino72.location.utils.LocationState
import com.shino72.location.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LocationViewModel
@Inject
constructor(
   private val locationRepository: LocationRepository
) : ViewModel()
{
    private val _location = MutableStateFlow(LocationState())
    val location : StateFlow<LocationState> = _location

    suspend fun getLocation() {
        locationRepository.getCurrentLocation().onEach {state ->
            when(state)
            {
                is Status.Loading -> {
                    _location.value = LocationState(isLoading = true)
                }
                is Status.Error -> {
                    _location.value = LocationState(error = state.message ?: "")
                }
                is Status.Success -> {
                    _location.value = LocationState(data = state.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _distance = MutableLiveData<Double>()
    val distance : LiveData<Double> = _distance

    fun distanceInKilometerByHaversine(x1: Double, y1: Double, x2: Double, y2: Double) {
        val distance: Double
        val radius = 6371.0 // 지구 반지름(km)
        val toRadian = Math.PI / 180
        val deltaLatitude = Math.abs(x1 - x2) * toRadian
        val deltaLongitude = Math.abs(y1 - y2) * toRadian
        val sinDeltaLat = Math.sin(deltaLatitude / 2)
        val sinDeltaLng = Math.sin(deltaLongitude / 2)
        val squareRoot = Math.sqrt(
            sinDeltaLat * sinDeltaLat +
                    Math.cos(x1 * toRadian) * Math.cos(x2 * toRadian) * sinDeltaLng * sinDeltaLng
        )
        distance = 2 * radius * Math.asin(squareRoot)
        _distance.value = distance
    }

}