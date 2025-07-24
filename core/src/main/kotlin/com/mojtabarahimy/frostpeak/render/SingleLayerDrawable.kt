package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class SingleLayerDrawable : MultipleLayerDrawable() {

    /**
     * Does not matter which draw*() method we use from parent class,
     * since we are actually calling draw*() methods in proper situations in [WorldRenderer].
     * This class is to simplify and avoid misunderstandings.
     */
    fun draw(batch: SpriteBatch, texture: TextureRegion, x: Float, y: Float) {
        drawBehindPlayer(batch, texture, x, y)
    }
}
