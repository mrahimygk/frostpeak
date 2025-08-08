package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.data.WeatherType


class WeatherSystem {

    private val weatherEffects = mutableMapOf<WeatherType, ParticleEffect>()

    private val darkOverlayTexture: Texture

    init {

        val particleEmitterPosition = Vector2(Gdx.graphics.height * 1.5f, Gdx.graphics.height * 2f)
        val particleImagePath = Gdx.files.internal("particles")

        val rainEffect = ParticleEffect()
        rainEffect.load(Gdx.files.internal("particles/rain.p"), particleImagePath)
        rainEffect.setPosition(particleEmitterPosition.x, particleEmitterPosition.y)
        rainEffect.start()

        val snowEffect = ParticleEffect()
        snowEffect.load(Gdx.files.internal("particles/snow.p"), particleImagePath)
        snowEffect.setPosition(particleEmitterPosition.x, particleEmitterPosition.y)
        snowEffect.start()

        weatherEffects[WeatherType.RAINY] = rainEffect
        weatherEffects[WeatherType.SNOW] = snowEffect

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0.3f) // Dark with 30% transparency
        pixmap.fill()
        darkOverlayTexture = Texture(pixmap)
        pixmap.dispose()
    }

    fun updateAndRender(batch: SpriteBatch, weather: WeatherType) {
        if (weather != WeatherType.SUNNY) {
            weatherEffects[weather]?.update(Gdx.graphics.deltaTime)
            weatherEffects[weather]?.draw(batch)

            batch.setColor(1f, 1f, 1f, 0.6f)
            batch.draw(darkOverlayTexture, 0f, 0f)
            batch.setColor(Color.WHITE)
        }
    }
}
