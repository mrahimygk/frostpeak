package com.mojtabarahimy.frostpeak.entities.tools

interface ToolTarget {
    fun onToolUsed(tool: Tool)
    val x: Float
    val y: Float
}
