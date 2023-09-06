package com.shino72.location.utils

import android.location.Location
import com.shino72.location.db.Entity.Plan

data class DBState (
    val db : List<Plan>?  = null,
    val error: String = "",
    val isLoading: Boolean = false
)