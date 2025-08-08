package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mojtabarahimy.frostpeak.data.WeatherType


class WeatherSystem(private val particleEffect: ParticleEffect) {

    private val darkOverlayTexture: Texture

    init {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0.3f) // Dark with 30% transparency
        pixmap.fill()
        darkOverlayTexture = Texture(pixmap)
        pixmap.dispose()
    }

    fun updateAndRender(batch: SpriteBatch, weather: WeatherType) {
        if (weather == WeatherType.RAINY) {
            particleEffect.update(Gdx.graphics.deltaTime)
            particleEffect.draw(batch)

            batch.setColor(1f, 1f, 1f, 0.6f)
            batch.draw(darkOverlayTexture, 0f, 0f)
            batch.setColor(Color.WHITE)
        }
    }
}
