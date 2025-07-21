package com.mojtabarahimy.frostpeak

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.controller.CameraController
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.input.PlayerInputProcessor
import com.mojtabarahimy.frostpeak.interaction.InteractionSystem
import com.mojtabarahimy.frostpeak.util.Constants

class FrostPeakGame : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport
    private lateinit var player: Player
    private lateinit var playerInputProcessor: PlayerInputProcessor
    private lateinit var cameraController: CameraController

    private lateinit var map: TiledMap
    private lateinit var mapRenderer: TiledMapRenderer

    private val beforePlayerLayers = arrayOf("ground", "trees", "houseBase")
    private val afterPlayerLayers = arrayOf("abovePlayer")

    private lateinit var collisionSystem: CollisionSystem
    private lateinit var interactionSystem: InteractionSystem

    private lateinit var shapeRenderer: ShapeRenderer

    override fun create() {
        batch = SpriteBatch()

        camera = OrthographicCamera()

        viewport = FitViewport(Constants.worldWidth, Constants.worldHeight, camera)

        viewport.apply()
        camera.setToOrtho(false, viewport.worldWidth, viewport.worldHeight)

        cameraController = CameraController(camera, viewport)

        val texture = Texture("player_sheet.png")
        val walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.wav"))
        map = TmxMapLoader().load("maps/main_house_outdoor.tmx")

        collisionSystem = CollisionSystem(map)
        interactionSystem = InteractionSystem(map)

        player = Player(texture, walkSound, collisionSystem)

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
        playerInputProcessor = PlayerInputProcessor(
            playerMovement = { delta, dx, dy ->
                player.update(delta, dx, dy)
            },
            onInteract = {
                interactionSystem.handleInteraction(player.getInteractionBounds())
            })

        Gdx.input.inputProcessor = playerInputProcessor

        mapRenderer = OrthogonalTiledMapRenderer(map, 1f)

        val objects = map.layers.get("objects").objects
        val spawn: MapObject = objects.get("player_spawn")
        val x: Float = spawn.properties["x"] as Float
        val y: Float = spawn.properties["y"] as Float
        player.setPosition(x, y)
        shapeRenderer = ShapeRenderer()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        playerInputProcessor.update(delta, Constants.speed)

        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        cameraController.update(delta, player.getCameraFocusX(), player.getCameraFocusY())

        renderMapBeforePlayer()

        batch.projectionMatrix = camera.combined
        batch.begin()
        player.draw(batch)
        batch.end()

        renderMapAfterPlayer()

        shapeRenderer.projectionMatrix = camera.combined
        collisionSystem.drawDebug(shapeRenderer)
        player.drawDebug(shapeRenderer)
    }

    private fun renderMapBeforePlayer() {
        mapRenderer.setView(camera)
        mapRenderer.render(beforePlayerLayers.toMapIndices(map))
    }

    private fun renderMapAfterPlayer() {
        mapRenderer.setView(camera)
        mapRenderer.render(afterPlayerLayers.toMapIndices(map))
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

private fun Array<String>.toMapIndices(map: TiledMap): IntArray {
    return mapNotNull { name -> map.layers.getIndex(name) }.toIntArray()
}
