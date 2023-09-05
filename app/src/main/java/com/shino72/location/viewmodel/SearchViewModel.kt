package com.shino72.location.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shino72.location.repository.KakaoRepository
import com.shino72.location.service.data.kakao.Response
import com.shino72.location.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel
@Inject
constructor(private val kakaorespository : KakaoRepository) : ViewModel() {
    private val _place =  MutableLiveData<Status<Response>>()
    val place : LiveData<Status<Response>> = _place

    fun getPlace(places : String) {
        _place.value = null
        viewModelScope.launch {
            _place.value = Status.Loading()
            val response = kakaorespository.getPlace(places)
            if(response.isSuccessful) {
                _place.value = Status.Success(response.body())
            }
            else {
                _place.value = Status.Error(response.errorBody().toString())
            }
        }
    }
}