package com.shino72.location.viewmodel

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
}