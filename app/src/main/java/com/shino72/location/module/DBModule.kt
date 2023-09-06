package com.shino72.location.module

import android.content.Context
import androidx.room.Room
import com.shino72.location.db.PlanDatabase
import com.shino72.location.db.dao.PlanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DBModule {

    @Singleton
    @Provides
    fun providePlanDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
            context,
            PlanDatabase::class.java,
            "plan_db"
        ).fallbackToDestructiveMigration()
        .build()


    @Provides
    fun providePlanDao(database: PlanDatabase) = database.planDao()
}