package com.shino72.location.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shino72.location.db.Entity.Plan
import com.shino72.location.db.dao.PlanDao

@Database(entities = [Plan::class], version = 1, exportSchema = false)
abstract class PlanDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
}