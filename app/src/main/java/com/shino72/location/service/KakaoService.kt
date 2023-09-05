package com.shino72.location.service

import com.shino72.location.service.data.kakao.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoService {
    @GET("keyword.json")
    suspend fun getResponse(@Header("Authorization") Authorization : String, @Query("page") page : String, @Query("size") size : String, @Query("sort") sort:String, @Query("query") query : String): retrofit2.Response<Response>
}