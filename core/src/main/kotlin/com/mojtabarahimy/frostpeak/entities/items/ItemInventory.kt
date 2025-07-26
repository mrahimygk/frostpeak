package com.mojtabarahimy.frostpeak.entities.items

import com.badlogic.gdx.graphics.Texture

class ItemInventory {
    private val slots = mutableListOf<ItemStack>()
    val texture = Texture("inventory/inventory_onscreen.png")

    fun addItem(item: Item, quantity: Int = 1) {
        val stack = slots.find { it.item.id == item.id }
        if (stack != null) {
            stack.add(quantity)
        } else if (slots.size < 6) {
            slots.add(ItemStack(item, quantity))
        } else {
            println("Inventory full!")
        }
    }

    fun getSlots(): List<ItemStack> = slots
}
