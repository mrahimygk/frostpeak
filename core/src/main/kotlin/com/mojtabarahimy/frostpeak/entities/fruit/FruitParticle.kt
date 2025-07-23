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

    private var scaleX = 1f
    private var scaleY = 1f
    private var squashTimer = 0f

    private var elapsed = 0f
    var isAlive = true

    fun update(delta: Float) {
        velocity.y -= 300f * delta // gravity
        position.mulAdd(velocity, delta)

        if (velocity.y < 0 && position.y > 0f) {
            scaleX = 0.9f
            scaleY = 1.2f
        }

        if (position.y < 1f && velocity.y < 0f && squashTimer == 0f) {
            scaleX = 1.3f
            scaleY = 0.7f
            squashTimer = 0.1f // 100ms squash
            velocity.y = 0f // simulate hit ground
        }

        if (squashTimer > 0f) {
            squashTimer -= delta
            if (squashTimer <= 0f) {
                scaleX = 1f
                scaleY = 1f
            }
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
