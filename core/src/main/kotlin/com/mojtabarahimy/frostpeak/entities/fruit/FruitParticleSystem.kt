package com.mojtabarahimy.frostpeak.entities.fruit

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class FruitParticleSystem {

    private val particles = mutableListOf<FruitParticle>()

    fun spawn(position: Vector2, count: Int, texture: TextureRegion) {
        repeat(count) {
            particles.add(FruitParticle(position.cpy(), texture))
        }
    }

    fun update(delta: Float) {
        particles.forEach { it.update(delta) }
        particles.removeIf { !it.isAlive }
    }

    fun drawBehindPlayer(batch: SpriteBatch, playerY: Float) {
        particles.filter { it.isBehindPlayer(playerY) }
            .forEach { it.draw(batch) }
    }

    fun drawAbovePlayer(batch: SpriteBatch, playerY: Float) {
        particles.filterNot { it.isBehindPlayer(playerY) }
            .forEach { it.draw(batch) }
    }
}
