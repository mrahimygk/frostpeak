package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.entities.items.ItemInventory
import com.mojtabarahimy.frostpeak.entities.npc.Npc
import com.mojtabarahimy.frostpeak.entities.tools.ToolInventory
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget

class Player(
    val texture: Texture,
    val walkSound: Sound,
    private val collisionSystem: CollisionSystem
) : Person by Npc(texture, walkSound, collisionSystem) {

    val toolInventory = ToolInventory()
    val itemInventory = ItemInventory()

    fun switchToolForward() {
        toolInventory.nextTool()
    }

    fun switchToolBackward() {
        toolInventory.previousTool()
    }

    fun useTool(targets: List<ToolTarget>) {
        toolInventory.useSelectedTool(detectToolTarget(targets))
    }

    private fun detectToolTarget(objects: List<ToolTarget>): ToolTarget? {
        val detectionDistance = 32f
        val playerFacingX = x + when (currentDirection) {
            Direction.LEFT -> -detectionDistance
            Direction.RIGHT -> detectionDistance
            else -> 0f
        }
        val playerFacingY = y + when (currentDirection) {
            Direction.UP -> detectionDistance
            Direction.DOWN -> -detectionDistance
            else -> 0f
        }

        return objects.firstOrNull {
            Vector2(it.x, it.y).dst(playerFacingX, playerFacingY) < 20f
        }
    }
}
