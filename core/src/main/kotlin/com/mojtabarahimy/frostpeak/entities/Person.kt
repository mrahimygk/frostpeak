package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.collision.CollisionSystem

interface Person : Talkable {
    val name: String
    var x: Float
    var y: Float
    var currentDirection: Direction

    fun update(delta: Float, dx: Float, dy: Float, collisionSystem: CollisionSystem)
    fun moveToward(targetX: Float, targetY: Float, delta: Float, collisionSystem: CollisionSystem)
    fun draw(batch: SpriteBatch)
    fun drawDebug(shapeRenderer: ShapeRenderer)
    fun getInteractionBounds(): Rectangle
    fun getPassiveInteractionBounds(): Rectangle

    fun getCameraFocusX(): Float
    fun getCameraFocusY(): Float

    fun setPosition(x: Float, y: Float)
    fun setDirection(direction: Direction)
}

fun Person.atTarget(targetX: Float, targetY: Float, tolerance: Float = 0.05f): Boolean {
    return (kotlin.math.abs(x - targetX) <= tolerance &&
        kotlin.math.abs(y - targetY) <= tolerance)
}

