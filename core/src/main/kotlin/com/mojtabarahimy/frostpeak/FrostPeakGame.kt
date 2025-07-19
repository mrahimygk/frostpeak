package com.mojtabarahimy.frostpeak

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.input.PlayerInputProcessor
import com.mojtabarahimy.frostpeak.util.Constants

class FrostPeakGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var camera: OrthographicCamera
    lateinit var viewport: FitViewport
    lateinit var player: Player
    private val targetCamPos = Vector2()

    lateinit var playerInputProcessor: PlayerInputProcessor

    override fun create() {
        batch = SpriteBatch()

        camera = OrthographicCamera()

        viewport = FitViewport(Constants.worldWidth, Constants.worldHeight, camera)

        viewport.apply()
        camera.setToOrtho(false, viewport.worldWidth, viewport.worldHeight)

        val texture = Texture("player.png") // Put this image in core/assets
        player = Player(texture)

        camera.position.set(
            player.x + player.texture.width / 2f,
            player.y + player.texture.height / 2f,
            0f
        )
        camera.update()

        //Gdx.app.log("Frostpeak", "Texture loaded: ${playerTexture.width}x${playerTexture.height}")
/*
        playerX = 100f
        playerY = 100f
*/
        playerInputProcessor = PlayerInputProcessor { dx, dy ->
            player.move(dx, dy)
        }

        Gdx.input.inputProcessor = playerInputProcessor
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        playerInputProcessor.update(delta, Constants.speed)

        val safeMarginX = viewport.worldWidth / 3f
        val safeMarginY = viewport.worldHeight / 3f

        val camLeft = camera.position.x - viewport.worldWidth / 2f + safeMarginX
        val camRight = camera.position.x + viewport.worldWidth / 2f - safeMarginX
        val camBottom = camera.position.y - viewport.worldHeight / 2f + safeMarginY
        val camTop = camera.position.y + viewport.worldHeight / 2f - safeMarginY

        targetCamPos.set(camera.position.x, camera.position.y)

        if (player.x < camLeft) {
            targetCamPos.x = player.x - safeMarginX + viewport.worldWidth / 2f
        } else if (player.x > camRight) {
            targetCamPos.x = player.x + safeMarginX - viewport.worldWidth / 2f
        }

        if (player.y < camBottom) {
            targetCamPos.y = player.y - safeMarginY + viewport.worldHeight / 2f
        } else if (player.y > camTop) {
            targetCamPos.y = player.y + safeMarginY - viewport.worldHeight / 2f
        }

        val lerpFactor = 5f * Gdx.graphics.deltaTime  // Adjust this for smoothness

        camera.position.x += (targetCamPos.x - camera.position.x) * lerpFactor
        camera.position.y += (targetCamPos.y - camera.position.y) * lerpFactor
        camera.update()

        ScreenUtils.clear(0.5f, 0.8f, 1f, 1f) // sky blue background
        batch.projectionMatrix = camera.combined

        batch.begin()
        player.draw(batch)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        player.texture.dispose()
    }
}
