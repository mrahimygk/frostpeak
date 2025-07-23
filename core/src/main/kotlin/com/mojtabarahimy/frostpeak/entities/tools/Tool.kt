package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

interface Tool {
    val name: String
    val texture: Texture
    fun use(target: ToolTarget?)
}
