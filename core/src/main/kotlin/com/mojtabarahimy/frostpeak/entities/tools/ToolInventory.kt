package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class ToolInventory {
    val tools = mutableListOf<Tool>()
    private var selectedIndex = 0

    val texture = Texture("inventory/inventory_onscreen.png")

    init {
        tools.add(Axe())
        tools.add(Knife())
        tools.add(Shovel())
        tools.add(Pickax())
    }

    val selectedTool: Tool?
        get() = if (tools.isNotEmpty()) tools[selectedIndex % tools.size] else null

    fun nextTool() {
        selectedIndex = (selectedIndex + 1) % tools.size
        println("Tool is ${selectedTool?.name}")
    }

    fun previousTool() {
        selectedIndex = (selectedIndex - 1 + tools.size) % tools.size
        println("Tool is ${selectedTool?.name}")
    }

    fun useSelectedTool(toolTarget: ToolTarget?) {
        selectedTool?.use(toolTarget)
    }

    fun selectTool(i: Int) {
        selectedIndex = i
        println("Tool is ${selectedTool?.name}")

    }
}
