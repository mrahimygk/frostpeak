package com.mojtabarahimy.frostpeak.util.ktx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2


fun Rectangle.distanceToRectangleEdge(x: Float, y: Float): Float {
    val closestX = MathUtils.clamp(x, this.x, this.x + this.width)
    val closestY = MathUtils.clamp(y, this.y, this.y + this.height)
    return Vector2(closestX, closestY).dst(x, y)
}
