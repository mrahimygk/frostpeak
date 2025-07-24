package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class SingleLayerDrawable(private val shouldDrawBehind: Boolean) : Drawable {
    override fun drawBehindPlayer(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float) {
        if (shouldDrawBehind) {

        }
    }

    override fun drawAbovePlayer(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float) {
        if (!shouldDrawBehind) {

        }
    }
}
