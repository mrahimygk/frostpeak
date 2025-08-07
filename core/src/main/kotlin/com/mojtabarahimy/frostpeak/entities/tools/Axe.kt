package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class Axe : Tool, XpGainer by XpGainerImpl()  {
    override val name = "Axe"

    override val texture: Texture
        get() = Texture("tools/ax.png")

    override val energyNeededPerSwing: Float
        get() = 5f

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging axe at nothing.")
        }
    }
}
