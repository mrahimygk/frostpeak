package com.mojtabarahimy.frostpeak.data

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mojtabarahimy.frostpeak.interaction.InteractableObject

class GroundHolesManager(private val holeList: List<InteractableObject.GroundInteractable>) {

    private val shallowHoleTexture = Texture("maps/ground/shallow_hole.png")
    private val shallowHoleWetTexture = Texture("maps/ground/shallow_hole_wet.png")
    private val shallowHoleLevelOneThirdTexture = Texture("maps/ground/shallow_hole_water_1.png")
    private val shallowHoleLevelTwoThirdTexture = Texture("maps/ground/shallow_hole_water_2.png")
    private val shallowHoleLevelThreeThirdTexture = Texture("maps/ground/shallow_hole_water_3.png")

    private val deepHoleTexture = Texture("maps/ground/deep_hole.png")

    fun dig(groundInteractable: InteractableObject.GroundInteractable): Boolean {
        groundInteractable.dig()
        return groundInteractable.digLevel > 0
    }

    fun update(delta: Float, weatherType: WeatherType) {
        when (weatherType) {
            WeatherType.SUNNY -> {
                drainHole(delta)
            }

            WeatherType.RAINY -> {
                fillWithWater(delta)
            }

            WeatherType.SNOW -> {

            }
        }
    }

    private fun decideWaterLevelSpeed(digLevel: Int, speedMultiplier: Float): Float {
        return speedMultiplier * when (digLevel) {
            0 -> 0f
            1 -> 0.04f
            2 -> 0.02f
            else -> TODO()
        }
    }

    private fun calculateNewWaterLevel(currentLevel: Float, speed: Float, delta: Float): Float {
        return currentLevel + speed * delta
    }

    private fun drainHole(delta: Float) {
        holeList.forEach { hole ->
            val drain = -1f * decideWaterLevelSpeed(hole.digLevel, 2f)
            hole.waterLevel = calculateNewWaterLevel(hole.waterLevel, drain, delta).coerceAtLeast(0f)
        }
    }


    private fun fillWithWater(delta: Float) {
        holeList.forEach { hole ->
            val fillSpeed = decideWaterLevelSpeed(hole.digLevel, 1f)
            hole.waterLevel = calculateNewWaterLevel(hole.waterLevel, fillSpeed, delta).coerceAtMost(1f)
        }
    }

    fun render(batch: SpriteBatch) {
        holeList.forEach { hole ->
            hole.bounds.run {
                when (hole.digLevel) {
                    0 -> return
                    1 -> {
                        val waterLevel = hole.waterLevel
                        when {
                            waterLevel == 0f -> batch.draw(shallowHoleTexture, x, y)
                            waterLevel < 1f / 3f -> batch.draw(shallowHoleWetTexture, x, y)
                            waterLevel < 2f / 3f -> batch.draw(
                                shallowHoleLevelOneThirdTexture,
                                x,
                                y
                            )

                            waterLevel < 1f -> batch.draw(shallowHoleLevelTwoThirdTexture, x, y)
                            else -> batch.draw(shallowHoleLevelThreeThirdTexture, x, y)
                        }
                    }

                    2 -> {
                        val waterLevel = hole.waterLevel
                        when {
                            waterLevel == 0f -> batch.draw(deepHoleTexture, x, y)
                            else -> TODO()
                        }
                    }
                    else -> TODO()
                }
            }
        }
    }


}
