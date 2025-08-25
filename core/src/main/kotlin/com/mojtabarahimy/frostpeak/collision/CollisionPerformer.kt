package com.mojtabarahimy.frostpeak.collision

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.entities.Direction

interface CollisionPerformer {
    fun drawDebug(shapeRenderer: ShapeRenderer, collisionBounds: Rectangle)
    fun getInteractionBounds(collisionBounds: Rectangle, currentDirection: Direction): Rectangle
    fun getPassiveInteractionBounds(collisionBounds: Rectangle): Rectangle
}
