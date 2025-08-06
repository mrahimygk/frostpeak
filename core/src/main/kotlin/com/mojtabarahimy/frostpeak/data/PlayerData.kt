package com.mojtabarahimy.frostpeak.data

class PlayerData(
    val maxEnergy: Float = 100f,
    currentEnergy: Float = 100f,
    val currentMoney : Int = 0
) {

    var money: Int = currentMoney
    var energy = currentEnergy

    fun addMoney(amount: Int): Int {
        money += amount
        return money
    }

    fun spendMoney(amount: Int): Boolean {
        return if (money >= amount) {
            money -= amount
            true
        } else false
    }

    fun useEnergy(amount: Float) {
        energy = (energy - amount).coerceAtLeast(0f)
    }

    fun restoreEnergy(amount: Float) {
        energy = (energy + amount).coerceAtMost(maxEnergy)
    }

    fun refillEnergy() {
        energy = maxEnergy
    }

    val isExhausted: Boolean
        get() = energy <= 0f
}
