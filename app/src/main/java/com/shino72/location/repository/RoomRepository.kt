package com.shino72.location.repository

import com.shino72.location.db.Entity.Plan
import com.shino72.location.db.dao.PlanDao
import com.shino72.location.utils.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RoomRepository@Inject constructor(private val planDao: PlanDao) {
    fun getAllPlans(): Flow<Status<List<Plan>>> = flow {
        try {
            val plans = planDao.getAllPlans()
            emit(Status.Success(plans))
        } catch (e: Exception) {
            emit(Status.Error(e.localizedMessage ?: ""))
        }
    }

    suspend fun insertPlan(plan: Plan) : Status<Unit> {
        return try {
            planDao.insertPlan(plan)
            Status.Success(Unit)
        }
        catch (e : Exception){
            Status.Error(e.localizedMessage ?: "")
        }

    }

    suspend fun updatePlan(plan:Plan) : Status<Unit> {
        return try{
            planDao.updatePlan(plan)
            Status.Success(Unit)
        }
        catch (e : Exception) {
            Status.Error(e.localizedMessage ?: "")
        }
    }

    suspend fun deletePlan(plan: Plan) : Status<Unit>{
        return try {
            planDao.deletePlan(plan)
            Status.Success(Unit)
        }
        catch (e : Exception){
            Status.Error(e.localizedMessage ?: "")
        }
    }

    suspend fun getFinishedPlans() : Flow<Status<List<Plan>>> = flow {
        try {
            val plans = planDao.getFinishedPlan()
            emit(Status.Success(plans))
        } catch (e: Exception) {
            emit(Status.Error(e.localizedMessage ?: ""))
        }
    }

    suspend fun getNotCompletedPlans() : Flow<Status<List<Plan>>> = flow {
        try {
            val plans = planDao.getNotCompletedPlan()
            emit(Status.Success(plans))
        } catch (e: Exception) {
            emit(Status.Error(e.localizedMessage ?: ""))
        }
    }
}