package com.mojtabarahimy.frostpeak.entities.crops

interface Growable {
    var growthStage: Int
    fun grow()
}
