package com.mojtabarahimy.frostpeak.entities.fruit.harvest

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

data class DroppedItem(
    val texture: TextureRegion,
    var x: Float,
    var y: Float,
    val itemId: String
) {
    fun render(batch: SpriteBatch) {
        batch.draw(texture, x, y)
    }

    //TODO: pickup()
}
