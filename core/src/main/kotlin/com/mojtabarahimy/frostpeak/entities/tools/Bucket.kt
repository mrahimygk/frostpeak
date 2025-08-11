package com.mojtabarahimy.frostpeak.entities.tools

import com.badlogic.gdx.graphics.Texture

class Bucket : Tool, XpGainer by XpGainerImpl(), ChangeableState by ChangeableStateImpl() {
    override val name = "Bucket"

    override val texture: Texture
        get() = if (stateChanged) {
            Texture("tools/bucket_full.png")
        } else {
            Texture("tools/bucket.png")
        }

    override val energyNeededPerSwing: Float
        get() = 6f

    val isFilled : Boolean
        get() = stateChanged

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging bucket at nothing.")
        }
    }
}
