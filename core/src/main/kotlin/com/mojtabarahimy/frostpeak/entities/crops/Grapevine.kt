package com.mojtabarahimy.frostpeak.entities.crops

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class Grapevine(
    private val position: Vector2,
    private val atlas: TextureAtlas,
    private var growthStage: Int = 0,
    private var daysForNextStage: Int = 2
) {

    private var currentStageFull: TextureAtlas.AtlasRegion = getCurrentStageRegion()
    private var currentStageTrunk = getCurrentStageTrunk()
    private var currentStageAbovePlayer = getCurrentStageAbovePlayer()

    private fun getCurrentStageRegion(): TextureAtlas.AtlasRegion {
        val region = atlas.regions.get(growthStage)
        return region
    }

    private fun grow() {
        if (growthStage < atlas.regions.size - 1) {
            growthStage++
            currentStageFull = getCurrentStageRegion()
            currentStageTrunk = getCurrentStageTrunk()
            currentStageAbovePlayer = getCurrentStageAbovePlayer()
        }
    }

    private fun getCurrentStageTrunk() = TextureRegion(
        currentStageFull,
        0,
        currentStageFull.regionHeight - 24,
        currentStageFull.regionWidth,
        24
    )

    private fun getCurrentStageAbovePlayer() = TextureRegion(
        currentStageFull,
        0,
        0,
        currentStageFull.regionWidth,
        currentStageFull.regionHeight - 24
    )

    fun checkGrowth(daysPassed: Int) {
        if (daysPassed % daysForNextStage == 0) grow()
    }

    fun dispose() {
        atlas.dispose()
    }

    fun drawBase(batch: SpriteBatch) {
        batch.draw(currentStageTrunk, position.x, position.y)
    }

    fun drawAbovePlayer(batch: SpriteBatch) {
        batch.draw(currentStageAbovePlayer, position.x, position.y + 24)
    }

    fun getCollisionBounds() =
        Rectangle(
            position.x + currentStageFull.regionWidth / 2.75f,
            position.y,
            currentStageFull.regionWidth.toFloat() / 2.75f,
            currentStageFull.regionHeight.toFloat() / 10
        )

}
