package com.mojtabarahimy.frostpeak.render.anim

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.math.Rectangle

class BreakableStone(
    val name: String,
    private val bounds: Rectangle,
) {

    private val breakAtlas = TextureAtlas(Gdx.files.internal("stones/${name}_atlas.atlas"))

    private val breakAnimation =
        Animation(0.1f, breakAtlas.regions, Animation.PlayMode.NORMAL)

    private var breaking = false
    private var stateTime = 0f
    private var isDestroyed = false

    fun update(delta: Float) {
        if (breaking) {
            stateTime += delta
            if (breakAnimation.isAnimationFinished(stateTime)) {
                isDestroyed = true
            }
        }
    }

    fun render(batch: SpriteBatch) {
        if (breaking && !isDestroyed) {
            val frame: AtlasRegion = breakAnimation.getKeyFrame(stateTime) ?: return
            batch.draw(frame, bounds.x + frame.offsetX, bounds.y + frame.offsetY)
        }
    }

    fun breakStone() {
        breaking = true
        stateTime = 0f
    }
}
