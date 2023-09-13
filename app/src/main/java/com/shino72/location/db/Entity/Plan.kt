package com.shino72.location.db.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var place : String? = null,
    var x : String,
    var y : String,
    var contents : String,
    var year : String,
    var month : String,
    var dayOfMonth : String,
    var hour : String,
    var minute : String,
    var status : String = "예정",
    var timestamp : Long,
): java.io.Serializable
