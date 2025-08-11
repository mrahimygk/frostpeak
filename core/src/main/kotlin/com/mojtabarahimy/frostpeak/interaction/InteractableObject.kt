package com.mojtabarahimy.frostpeak.interaction

import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.entities.tools.Bucket
import com.mojtabarahimy.frostpeak.entities.tools.Pickax
import com.mojtabarahimy.frostpeak.entities.tools.Shovel
import com.mojtabarahimy.frostpeak.entities.tools.Tool
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget

sealed class InteractableObject {
    abstract val name: String
    abstract val type: InteractableType?
    abstract val bounds: Rectangle
    abstract var onInteract: (() -> Boolean)?

    data class BasicInteractable(
        override val name: String,
        override val type: InteractableType?,
        override val bounds: Rectangle,
        override var onInteract: (() -> Boolean)? = null
    ) : InteractableObject()

    data class FountainInteractable(
        override val name: String,
        override val type: InteractableType?,
        override val bounds: Rectangle,
        override var onInteract: (() -> Boolean)? = null
    ) : InteractableObject(), ToolTarget {

        override fun onToolUsed(tool: Tool) {
            println("${tool.name} used on ${name}")
            if (tool is Bucket) {
                tool.gainXP()
                onInteract?.invoke()
            }
        }

        override val rect: Rectangle
            get() = bounds
    }

    data class StoneInteractable(
        override val name: String,
        override val type: InteractableType?,
        override val bounds: Rectangle,
        val maxHp: Int,
        override var onInteract: (() -> Boolean)? = null
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

    data class GroundInteractable(
        override val name: String,
        override val type: InteractableType?,
        override val bounds: Rectangle,
        var digLevel: Int,
        var waterLevel: Float = 0f,
        override var onInteract: (() -> Boolean)? = null
    ) : InteractableObject(), ToolTarget {

        fun dig() {
            this.digLevel++
        }

        override fun onToolUsed(tool: Tool) {
            println("${tool.name} used on ${name}")
            if (tool is Shovel) {
                tool.gainXP()
                onInteract?.invoke()
            }
        }

        override val rect: Rectangle
            get() = bounds
    }
}
