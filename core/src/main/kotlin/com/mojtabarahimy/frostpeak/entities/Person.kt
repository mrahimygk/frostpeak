package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

interface Person {
    var x: Float
    var y: Float
    var currentDirection: Direction

    fun update(delta: Float, dx: Float, dy: Float)
    fun draw(batch: SpriteBatch)
    fun drawDebug(shapeRenderer: ShapeRenderer)
    fun getInteractionBounds(): Rectangle

    fun getCameraFocusX(): Float
    fun getCameraFocusY(): Float

    fun setPosition(x: Float, y: Float)
    fun setDirection(direction: Direction)
}
