package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

open class MultipleLayerDrawable : Drawable {
    override fun drawBehindPlayer(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float) {
        batch.draw(texture, x, y)
    }

    override fun drawAbovePlayer(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float) {
        batch.draw(texture, x, y)
    }
}
