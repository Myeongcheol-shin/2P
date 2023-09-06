package com.shino72.location.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.shino72.location.db.Entity.Plan

@Dao
interface PlanDao {

    @Insert
    suspend fun insertPlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Query("SELECT * FROM plans")
    suspend fun getAllPlans(): List<Plan>
}
