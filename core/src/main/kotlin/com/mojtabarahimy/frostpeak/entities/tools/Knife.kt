package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class Knife : Tool , XpGainer by XpGainerImpl() {
    override val name = "Knife"

    override val texture: Texture
        get() = Texture("tools/knife.png")

    override val energyNeededPerSwing: Float
        get() = 2f

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging Knife at nothing.")
        }
    }
}
