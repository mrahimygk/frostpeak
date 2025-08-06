package com.mojtabarahimy.frostpeak.entities.tools

class XpGainerImpl(
    currentLevel: Int = 1,
    currentXp: Int = 0,
) : XpGainer {
    override var level: Int = currentLevel
    override var xp: Int = currentXp

    override val xpToNextLevel: Int
        get() = 5

    override fun gainXP() {
        xp++
        println("${this.javaClass.name} gainXP to $xp!")
        if (xp >= xpToNextLevel) {
            levelUp()
        }
    }

    override fun levelUp() {
        level++
        xp = 0
        println("${this.javaClass.name} leveled up to $level!")
    }
}
