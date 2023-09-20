package com.shino72.location.db.dao

import androidx.room.*
import com.shino72.location.db.Entity.Plan

@Dao
interface PlanDao {

    @Insert
    suspend fun insertPlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Query("SELECT * FROM plans ORDER BY timestamp ASC" )
    suspend fun getAllPlans(): List<Plan>

    @Update
    suspend fun updatePlan(plan:Plan)

    /// 미완료 상태인 plans 반환
    @Query("SELECT * FROM plans WHERE status == '예정'")
    suspend fun getNotCompletedPlan() : List<Plan>

    /// 완료된 plans 반환
    @Query("SELECT * FROM plans WHERE status == '완료'")
    suspend fun getFinishedPlan() : List<Plan>

    /// 현재 시간 이후 + 완료되지 않은 플랜 가져오기
    @Query("SELECT * FROM plans WHERE timestamp > :currentTimeInMillis AND status = '예정'")
    suspend fun getPlansAfterTimestamp(currentTimeInMillis: Long): List<Plan>

}
