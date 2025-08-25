package com.mojtabarahimy.frostpeak.collision

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.entities.Direction

class BasicCollisionPerformer : CollisionPerformer {

    override fun drawDebug(shapeRenderer: ShapeRenderer, collisionBounds: Rectangle) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.MAGENTA

        shapeRenderer.rect(
            collisionBounds.x,
            collisionBounds.y,
            collisionBounds.width,
            collisionBounds.height
        )

        shapeRenderer.end()
    }

    override fun getInteractionBounds(
        collisionBounds: Rectangle,
        currentDirection: Direction
    ): Rectangle {
        val interactionRange = 10f
        collisionBounds.run {
            return when (currentDirection) {
                Direction.UP -> Rectangle(x, y + height, width, interactionRange)
                Direction.DOWN -> Rectangle(x, y - interactionRange, width, interactionRange)
                Direction.LEFT -> Rectangle(x - interactionRange, y, interactionRange, height)
                Direction.RIGHT -> Rectangle(x + width, y, interactionRange, height)
            }
        }
    }

    override fun getPassiveInteractionBounds(collisionBounds: Rectangle): Rectangle =
        Rectangle(collisionBounds)

}
