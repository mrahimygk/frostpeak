package com.mojtabarahimy.frostpeak.collision

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle

sealed class Collider {
    data class Box(val rect: Rectangle) : Collider()
    data class Poly(val polygon: Polygon) : Collider()
}
