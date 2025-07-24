package com.mojtabarahimy.frostpeak.entities.crops

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.entities.tools.Axe
import com.mojtabarahimy.frostpeak.entities.tools.Tool
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget
import com.mojtabarahimy.frostpeak.render.Drawable
import com.mojtabarahimy.frostpeak.render.MultipleLayerDrawable

abstract class Tree(
    private val position: Vector2,
    private var daysForNextStage: Int,
    private var growthStage: Int = 0
) : Drawable by MultipleLayerDrawable(), Interactable, ToolTarget {
    abstract val atlas: TextureAtlas
    abstract val fruitTexture: TextureRegion

    private var currentStageFull: TextureAtlas.AtlasRegion = getCurrentStageRegion()
    private var currentStageTrunk = getCurrentStageTrunk()
    private var currentStageAbovePlayer = getCurrentStageAbovePlayer()

    private fun getCurrentStageRegion(): TextureAtlas.AtlasRegion {
        val region = atlas.regions.get(growthStage)
        return region
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

    override val x: Float
        get() = getCollisionBounds().x
    override val y: Float
        get() = getCollisionBounds().y

    override fun onToolUsed(tool: Tool) {
        if (tool is Axe) {
            //TODO: check for growth stage etc
            println("Using tool on a ${this.javaClass.simpleName}")
        }
    }

    private fun grow() {
        if (growthStage < atlas.regions.size - 1) {
            growthStage++
            currentStageFull = getCurrentStageRegion()
            currentStageTrunk = getCurrentStageTrunk()
            currentStageAbovePlayer = getCurrentStageAbovePlayer()
        }
    }

    fun checkGrowth(daysPassed: Int) {
        if (daysPassed % daysForNextStage == 0) grow()
    }

    fun drawBehindPlayer(batch: SpriteBatch) {
        drawBehindPlayer(batch, currentStageTrunk, position.x, position.y)
    }

    fun drawAbovePlayer(batch: SpriteBatch) {
        drawAbovePlayer(batch, currentStageAbovePlayer, position.x, position.y + 24)
    }

    fun getCollisionBounds() =
        Rectangle(
            position.x + currentStageFull.regionWidth / 2.75f,
            position.y,
            currentStageFull.regionWidth.toFloat() / 2.75f,
            currentStageFull.regionHeight.toFloat() / 10
        )

    fun dispose() {
        atlas.dispose()
    }

    fun isRipe() = growthStage == atlas.regions.size - 1
}
