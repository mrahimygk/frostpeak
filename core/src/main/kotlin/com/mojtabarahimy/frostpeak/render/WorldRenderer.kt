package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.controller.WorldCameraController
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.input.PlayerInputProcessor
import com.mojtabarahimy.frostpeak.interaction.InteractionSystem
import com.mojtabarahimy.frostpeak.map.GameMap
import com.mojtabarahimy.frostpeak.time.GameClock
import com.mojtabarahimy.frostpeak.util.Constants

class WorldRenderer(clock: GameClock) {


    private val batch = SpriteBatch()
    private val worldCamera = OrthographicCamera()
    private val worldViewport: FitViewport =
        FitViewport(Constants.worldWidth, Constants.worldHeight, worldCamera).also {
            it.apply()
            worldCamera.setToOrtho(false, it.worldWidth, it.worldHeight)
        }
    private var player: Player
    private var playerInputProcessor: PlayerInputProcessor
    private val worldCameraController = WorldCameraController(worldCamera, worldViewport)

    private val gameMap = GameMap()
    private val collisionSystem = CollisionSystem()
    private val interactionSystem = InteractionSystem()

    private val shapeRenderer = ShapeRenderer()

    private var font: BitmapFont

    init {

        val texture = Texture("player_sheet.png")
        val walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.wav"))


        gameMap.initMap(
            "maps/main_house_outdoor.tmx",
            beforePlayerLayers = arrayOf("ground", "trees", "houseBase"),
            afterPlayerLayers = arrayOf("abovePlayer"),
        )
        initSystems()

        player = Player(texture, walkSound, collisionSystem)


        worldCamera.position.set(
            player.x + player.texture.width / 2f,
            player.y + player.texture.height / 2f,
            0f
        )
        worldCamera.update()


        playerInputProcessor = PlayerInputProcessor(
            playerMovement = { delta, dx, dy ->
                player.update(delta, dx, dy)
            },
            onInteract = {
                interactionSystem
                    .handleInteraction(
                        player.getInteractionBounds(),
                        onNextMap =
                            { mapFilePath, beforePlayerLayers, afterPlayerLayers ->
                                gameMap.loadNewMap(
                                    mapFilePath,
                                    beforePlayerLayers,
                                    afterPlayerLayers,
                                )

                                initSystems()
                                spawnPlayer()
                            },

                        onNextDay = {
                            clock.incrementDay()
                            spawnPlayer()
                        }
                    )
            })

        Gdx.input.inputProcessor = playerInputProcessor
        spawnPlayer()

        font = BitmapFont()
        font.color = Color.WHITE
    }

    fun render(delta: Float) {

        playerInputProcessor.update(delta, Constants.speed)
        val interactionBounds = player.getInteractionBounds()

        worldCameraController.update(delta, player.getCameraFocusX(), player.getCameraFocusY())

        val interactableObject = interactionSystem.getNearbyInteraction(interactionBounds)
        gameMap.renderMapBeforePlayer(worldCamera)

        batch.projectionMatrix = worldCamera.combined
        batch.begin()
        player.draw(batch)
        batch.end()

        gameMap.renderMapAfterPlayer(worldCamera)

        batch.begin()
        interactionSystem.handleInteractionHint(
            interactionBounds,
            interactableObject,
            batch,
            font,
            delta
        )
        batch.end()


        shapeRenderer.projectionMatrix = worldCamera.combined
        collisionSystem.drawDebug(shapeRenderer)
        player.drawDebug(shapeRenderer)
    }

    private fun initSystems() {
        collisionSystem.initMap(gameMap.map)
        interactionSystem.initMap(gameMap.map)
    }


    private fun spawnPlayer() {
        gameMap.getSpawnPoint().run {
            player.setPosition(x, y)
        }
    }

    fun resize(width: Int, height: Int) {
        worldViewport.update(width, height)
    }

    fun dispose() {
        batch.dispose()
        player.texture.dispose()
        player.walkSound.dispose()
        gameMap.dispose()
    }
}
