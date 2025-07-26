package com.mojtabarahimy.frostpeak.entities.items

data class ItemStack(val item: Item, var quantity: Int) {
    fun add(amount: Int) {
        quantity += amount
    }
}
