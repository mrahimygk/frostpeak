package com.mojtabarahimy.frostpeak.entities.crops

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2

class Grapevine(
    private val position: Vector2,
    private val atlas: TextureAtlas,
    private var growthStage: Int = 0,
    private var weeksPassed: Int = 0,
    private var daysForNextStage: Int = 2
) {

    private fun grow() {
        weeksPassed++
        if (growthStage < atlas.regions.size - 1) {
            growthStage++
        }
    }

    fun checkGrowth(daysPassed: Int) {
        if (daysPassed % daysForNextStage == 0) grow()
    }

    fun dispose() {
        atlas.dispose()
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(atlas.regions.get(growthStage), position.x, position.y)
    }
}
