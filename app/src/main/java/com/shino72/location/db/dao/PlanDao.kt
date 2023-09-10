package com.shino72.location.db.dao

import androidx.room.*
import com.shino72.location.db.Entity.Plan

@Dao
interface PlanDao {

    @Insert
    suspend fun insertPlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Query("SELECT * FROM plans")
    suspend fun getAllPlans(): List<Plan>

    @Update
    suspend fun updatePlan(plan:Plan)
}
