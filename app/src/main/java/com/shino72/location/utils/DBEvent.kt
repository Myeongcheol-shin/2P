package com.shino72.location.utils

sealed class DBEvent {
    object LoadDB : DBEvent()
}