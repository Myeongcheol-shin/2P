package com.shino72.location.repository

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.shino72.location.utils.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation() : Flow<Status<Location?>> = flow{
        emit(Status.Loading())
        try {
            val locationResult = fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            locationResult?.let {
                emit(Status.Success(data = it))
            }

        } catch (e: Exception) {
            // 위치 정보 가져오기 실패 시 예외 처리
            emit(Status.Error(e.localizedMessage ?: ""))
        }
    }
}