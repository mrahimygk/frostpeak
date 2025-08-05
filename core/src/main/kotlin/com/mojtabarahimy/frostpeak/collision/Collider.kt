package com.mojtabarahimy.frostpeak.collision

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle

sealed class Collider {
    abstract val name: String?

    data class Box(override val name: String?, val rect: Rectangle) : Collider()
    data class Poly(override val name: String?, val polygon: Polygon) : Collider()
}
