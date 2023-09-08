package com.shino72.location.data

data class Date (
    val today : Int,
    val year : Int,
    val month : Int,
    val daysOfWeek : List<Pair<String, String>>
    )