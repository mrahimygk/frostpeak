package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

interface Tool {
    val name: String
    val texture: Texture
    val energyNeededPerSwing: Float
    fun use(target: ToolTarget?)
}
