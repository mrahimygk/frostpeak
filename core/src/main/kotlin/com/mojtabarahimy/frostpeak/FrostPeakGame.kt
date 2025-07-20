package com.mojtabarahimy.frostpeak

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.controller.CameraController
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.input.PlayerInputProcessor
import com.mojtabarahimy.frostpeak.util.Constants

class FrostPeakGame : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport
    private lateinit var player: Player
    private lateinit var playerInputProcessor: PlayerInputProcessor
    private lateinit var cameraController: CameraController

    override fun create() {
        batch = SpriteBatch()

        camera = OrthographicCamera()

        viewport = FitViewport(Constants.worldWidth, Constants.worldHeight, camera)

        viewport.apply()
        camera.setToOrtho(false, viewport.worldWidth, viewport.worldHeight)

        cameraController = CameraController(camera, viewport)

        val texture = Texture("player_sheet.png")
        val walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.wav"))

        player = Player(texture, walkSound)

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
        playerInputProcessor = PlayerInputProcessor { delta, dx, dy ->
            player.update(delta, dx, dy)
        }

        Gdx.input.inputProcessor = playerInputProcessor
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        playerInputProcessor.update(delta, Constants.speed)

        cameraController.update(delta, player.getCameraFocusX(), player.getCameraFocusY())

        ScreenUtils.clear(0.5f, 0.8f, 1f, 1f) // sky blue background
        batch.projectionMatrix = camera.combined

        batch.begin()
        player.draw(batch)
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        batch.dispose()
        player.texture.dispose()
        player.walkSound.dispose()

    }
}
