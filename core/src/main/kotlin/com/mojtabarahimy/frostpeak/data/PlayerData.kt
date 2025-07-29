package com.mojtabarahimy.frostpeak.data

class PlayerData {

    var money: Int = 0

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
}
