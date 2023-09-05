package com.shino72.location.service.data.kakao

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("documents") val documents : List<Place>,
)


data class Place(
    @SerializedName("place_name") val place_name : String,
    @SerializedName("x") val x : String,
    @SerializedName("y") val y : String,
)