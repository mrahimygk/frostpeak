package com.mojtabarahimy.frostpeak.entities.tools

interface XpGainer {
    var level: Int
    var xp: Int
    val xpToNextLevel: Int

    fun gainXP()
    fun levelUp()
}
