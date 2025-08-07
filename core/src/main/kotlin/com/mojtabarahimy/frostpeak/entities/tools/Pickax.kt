package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class Pickax : Tool, XpGainer by XpGainerImpl() {
    override val name = "Pickax"

    override val texture: Texture
        get() = Texture("tools/pickax.png")

    override val energyNeededPerSwing: Float
        get() = 6f

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging Pickax at nothing.")
        }
    }
}
