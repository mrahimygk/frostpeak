package com.mojtabarahimy.frostpeak.entities.tools

interface Tool {
    val name: String
    fun use(target: ToolTarget?)
}
