package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.time.GameClock
import com.mojtabarahimy.frostpeak.util.Constants

class HUDRenderer(
    private val clock: GameClock
) {
    private val uiCamera = OrthographicCamera()
    private val uiViewport: FitViewport =
        FitViewport(Constants.worldWidth, Constants.worldHeight, uiCamera)
    private val batch = SpriteBatch()


    private val font = BitmapFont().apply {
        color = Color.WHITE
    }

    fun render() {
        uiCamera.update()
        batch.projectionMatrix = uiCamera.combined
        batch.begin()
        clock.draw(batch, font)
        batch.end()
    }


    fun resize(width: Int, height: Int) {
        uiViewport.update(width, height)
    }

    fun dispose() {
        batch.dispose()
    }


}
