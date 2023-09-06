package com.shino72.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location (
    val status : Boolean,
    val placeName : String? = "",
    val x : String? = "",
    val y : String? = "",
) : Parcelable