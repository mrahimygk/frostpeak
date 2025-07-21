package com.mojtabarahimy.frostpeak

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.controller.CameraController
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.input.PlayerInputProcessor
import com.mojtabarahimy.frostpeak.interaction.InteractionSystem
import com.mojtabarahimy.frostpeak.map.GameMap
import com.mojtabarahimy.frostpeak.util.Constants

class FrostPeakGame : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport
    private lateinit var player: Player
    private lateinit var playerInputProcessor: PlayerInputProcessor
    private lateinit var cameraController: CameraController

    private val gameMap = GameMap()
    private val collisionSystem = CollisionSystem()
    private val interactionSystem = InteractionSystem()

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()

        camera = OrthographicCamera()

        viewport = FitViewport(Constants.worldWidth, Constants.worldHeight, camera)

        viewport.apply()
        camera.setToOrtho(false, viewport.worldWidth, viewport.worldHeight)

        cameraController = CameraController(camera, viewport)

        val texture = Texture("player_sheet.png")
        val walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.wav"))

        font = BitmapFont()
        font.color = Color.WHITE

        gameMap.initMap(
            "maps/main_house_outdoor.tmx",
            beforePlayerLayers = arrayOf("ground", "trees", "houseBase"),
            afterPlayerLayers = arrayOf("abovePlayer"),
        )
        collisionSystem.initMap(gameMap.map)
        interactionSystem.initMap(gameMap.map)

        player = Player(texture, walkSound, collisionSystem)

        camera.position.set(
            player.x + player.texture.width / 2f,
            player.y + player.texture.height / 2f,
            0f
        )
        camera.update()

        playerInputProcessor = PlayerInputProcessor(
            playerMovement = { delta, dx, dy ->
                player.update(delta, dx, dy)
            },
            onInteract = {
                interactionSystem.handleInteraction(player.getInteractionBounds())
            })

        Gdx.input.inputProcessor = playerInputProcessor
        spawnPlayer()
        shapeRenderer = ShapeRenderer()
    }

    private fun spawnPlayer() {
        gameMap.getSpawnPoint().run {
            player.setPosition(x, y)
        }
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        playerInputProcessor.update(delta, Constants.speed)
        val interactionBounds = player.getInteractionBounds()

        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        cameraController.update(delta, player.getCameraFocusX(), player.getCameraFocusY())
        val interactableObject = interactionSystem.getNearbyInteraction(interactionBounds)
        gameMap.renderMapBeforePlayer(camera)

        batch.projectionMatrix = camera.combined
        batch.begin()
        player.draw(batch)
        batch.end()

        gameMap.renderMapAfterPlayer(camera)

        batch.begin()
        interactionSystem.handleInteractionHint(
            interactionBounds,
            interactableObject,
            batch,
            font,
            delta
        )
        batch.end()

        shapeRenderer.projectionMatrix = camera.combined
        collisionSystem.drawDebug(shapeRenderer)
        player.drawDebug(shapeRenderer)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        batch.dispose()
        player.texture.dispose()
        player.walkSound.dispose()
        gameMap.dispose()
    }
}
