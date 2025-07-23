package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class Axe : Tool {
    override val name = "Axe"

    override val texture: Texture
        get() = Texture("tools/ax.png")

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging axe at nothing.")
        }
    }
}
