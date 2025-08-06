package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.math.Rectangle

interface ToolTarget {
    val rect: Rectangle
    fun onToolUsed(tool: Tool)
}
