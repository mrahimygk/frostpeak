package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class ToolInventory(toolStore: ToolStore) {
    val tools = mutableListOf<Tool>()

    private var selectedIndex = 0

    val texture = Texture("inventory/inventory_onscreen.png")

    init {
        tools.add(toolStore.allTools[0])
        tools.add(toolStore.allTools[1])
        tools.add(toolStore.allTools[2])
        tools.add(toolStore.allTools[3])
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

    fun fillBucket(): Boolean? {
        if (selectedTool is Bucket) {
            (selectedTool as? Bucket)?.changeState(true)
            return true
        }

        if (tools.filterIsInstance<Bucket>().isEmpty()) return null

        return false
    }

    fun emptyBucket() {
        if (selectedTool is Bucket) {
            (selectedTool as? Bucket)?.changeState(false)
        }
    }

    fun addTool(tool: Tool) {
        tools.add(tool)
    }
}
