package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

interface Drawable {
    fun drawBehindPlayer(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float)
    fun drawAbovePlayer(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float)
}
