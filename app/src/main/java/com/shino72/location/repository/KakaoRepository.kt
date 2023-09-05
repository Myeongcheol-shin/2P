package com.shino72.location.repository

import com.shino72.location.BuildConfig
import com.shino72.location.service.KakaoService
import retrofit2.Response
import javax.inject.Inject


class KakaoRepository @Inject constructor(private val apiService: KakaoService) {
    suspend fun getPlace(place :String): Response<com.shino72.location.service.data.kakao.Response> {
        val key = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}"
        return apiService.getResponse(key, "1","10", "accuracy", place)
    }
}