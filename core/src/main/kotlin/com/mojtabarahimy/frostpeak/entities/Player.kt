package com.mojtabarahimy.frostpeak.entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Player(
    val texture: Texture
) {

    var x = 100f
    var y = 100f
    private val width = texture.width.toFloat()
    private val height = texture.height.toFloat()

    fun move(dx: Float, dy: Float) {
        x += dx
        y += dy
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y)
    }

    fun getCameraFocusX(): Float = x + width / 2f
    fun getCameraFocusY(): Float = y + height / 2f
}
