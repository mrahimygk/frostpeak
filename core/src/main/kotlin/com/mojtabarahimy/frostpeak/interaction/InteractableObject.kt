package com.mojtabarahimy.frostpeak.interaction

import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.entities.tools.Pickax
import com.mojtabarahimy.frostpeak.entities.tools.Tool
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget

sealed class InteractableObject {
    abstract val name: String
    abstract val type: String?
    abstract val bounds: Rectangle
    abstract var onInteract: (() -> Unit)?

    data class BasicInteractable(
        override val name: String,
        override val type: String?,
        override val bounds: Rectangle,
        override var onInteract: (() -> Unit)? = null
    ) : InteractableObject()

    data class StoneInteractable(
        override val name: String,
        override val type: String?,
        override val bounds: Rectangle,
        val maxHp: Int,
        override var onInteract: (() -> Unit)? = null
    ) : InteractableObject(), ToolTarget {
        var hp = maxHp

        fun damage(amount: Int): Boolean {
            hp -= amount
            return hp < 1
        }

        override fun onToolUsed(tool: Tool) {
            println("${tool.name} used on ${name}")
            if (tool is Pickax) {
                tool.gainXP()
                if (damage(1)) onInteract?.invoke()
            }
        }

        override val rect: Rectangle
            get() = bounds
    }
}
