package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class Shovel : Tool, XpGainer by XpGainerImpl() {
    override val name = "Shovel"

    override val texture: Texture
        get() = Texture("tools/shovel.png")

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging Shovel at nothing.")
        }
    }
}
