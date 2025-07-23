package com.mojtabarahimy.frostpeak.entities.fruit

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
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

    private var scaleX = 1f
    private var scaleY = 1f
    private var squashTimeTotal = 0.2f
    private var squashTimeElapsed = 0f
    private var squashActive = false

    private var elapsed = 0f
    var isAlive = true

    fun update(delta: Float) {
        velocity.y -= 300f * delta // gravity
        position.mulAdd(velocity, delta)

        if (position.y < 1f && velocity.y < 0f && !squashActive) {
            velocity.y = 0f
            squashTimeElapsed = 0f
            squashActive = true
        }

        if (squashActive) {
            squashTimeElapsed += delta
            val t = squashTimeElapsed / squashTimeTotal

            if (t >= 1f) {
                squashActive = false
                scaleX = 1f
                scaleY = 1f
            } else {
                val interp = Interpolation.swingOut.apply(1f - t) // OR bounceOut
                scaleX = 1f + 0.3f * interp
                scaleY = 1f - 0.3f * interp
            }
        } else {
            scaleX = if (velocity.y < -50f) 0.9f else 1f
            scaleY = if (velocity.y < -50f) 1.2f else 1f
        }

        elapsed += delta

        if (elapsed > lifetime || position.y < 0f) {
            isAlive = false
        }

    }

    fun draw(batch: SpriteBatch) {
        batch.draw(
            texture,
            position.x,
            position.y,
            texture.regionWidth / 2f, texture.regionHeight / 2f, // origin
            texture.regionWidth.toFloat(),
            texture.regionHeight.toFloat(),
            scaleX,
            scaleY,
            0f
        )
    }

    fun isBehindPlayer(playerY: Float): Boolean {
        return position.y < playerY
    }
}
