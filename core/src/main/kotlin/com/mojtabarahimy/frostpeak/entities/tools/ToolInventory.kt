package com.mojtabarahimy.frostpeak.entities.tools

class ToolInventory {
    private val tools = mutableListOf<Tool>()
    private var selectedIndex = 0

    init {
        tools.add(Axe())
    }

    private val selectedTool: Tool?
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
}
