package com.mojtabarahimy.frostpeak.entities.fruit

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class FruitParticle(
    val position: Vector2,
    private val texture: TextureRegion
) {
    private var velocity = Vector2(
        MathUtils.random(-20f, 20f),
        MathUtils.random(80f, 120f)
    )

    private var lifetime = 2f // seconds

    private var elapsed = 0f
    var isAlive = true

    fun update(delta: Float) {
        velocity.y -= 300f * delta // gravity
        position.mulAdd(velocity, delta)

        elapsed += delta

        if (elapsed > lifetime || position.y < 0f) {
            isAlive = false
        }

    }

    fun draw(batch: SpriteBatch) {
        batch.draw(texture, position.x, position.y)
    }

    fun isBehindPlayer(playerY: Float): Boolean {
        return position.y < playerY
    }
}
