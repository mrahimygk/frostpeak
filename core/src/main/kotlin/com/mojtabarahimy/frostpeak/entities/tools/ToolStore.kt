package com.mojtabarahimy.frostpeak.entities.tools

class ToolStore {
    val allTools = mutableListOf<Tool>()

    init {
        allTools.add(Axe())
        allTools.add(Knife())
        allTools.add(Shovel())
        allTools.add(Pickax())
        allTools.add(Bucket())
    }

    fun addTool(tool: Tool) {
        allTools.add(tool)
    }
}
