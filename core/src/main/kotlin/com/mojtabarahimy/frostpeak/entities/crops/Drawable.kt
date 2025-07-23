package com.mojtabarahimy.frostpeak.entities.crops

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface Drawable {
    fun drawTrunk(batch: SpriteBatch)
    fun drawAbovePlayer(batch: SpriteBatch)
}
