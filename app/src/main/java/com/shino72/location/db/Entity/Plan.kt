package com.shino72.location.db.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val place : String? = null,
    val x : String,
    val y : String,
    val contents : String,
    val year : String,
    val month : String,
    val dayOfMonth : String,
    val hour : String,
    val minute : String,


)
