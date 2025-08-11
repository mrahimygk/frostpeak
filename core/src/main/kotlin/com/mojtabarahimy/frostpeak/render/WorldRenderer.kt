package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.controller.MapTransitionController
import com.mojtabarahimy.frostpeak.controller.WorldCameraController
import com.mojtabarahimy.frostpeak.controller.dialog.DialogManager
import com.mojtabarahimy.frostpeak.controller.npc.NpcController
import com.mojtabarahimy.frostpeak.data.GroundHolesManager
import com.mojtabarahimy.frostpeak.data.PlayerData
import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.entities.Player
import com.mojtabarahimy.frostpeak.entities.crops.Grapevine
import com.mojtabarahimy.frostpeak.entities.fruit.FruitParticleSystem
import com.mojtabarahimy.frostpeak.entities.fruit.harvest.DroppedItem
import com.mojtabarahimy.frostpeak.entities.items.Item
import com.mojtabarahimy.frostpeak.entities.tools.ToolTarget
import com.mojtabarahimy.frostpeak.input.PlayerInputProcessor
import com.mojtabarahimy.frostpeak.interaction.InteractableObject
import com.mojtabarahimy.frostpeak.interaction.InteractableType
import com.mojtabarahimy.frostpeak.interaction.InteractionSystem
import com.mojtabarahimy.frostpeak.map.GameMap
import com.mojtabarahimy.frostpeak.render.anim.BreakableStone
import com.mojtabarahimy.frostpeak.util.Constants

class WorldRenderer(
    private val clock: GameClock,
    private val playerData: PlayerData,
    private val dialogManager: DialogManager,
    private val weatherSystem: WeatherSystem
) {

    private val batch = SpriteBatch()
    private val worldCamera = OrthographicCamera()
    private val worldViewport: FitViewport =
        FitViewport(Constants.worldWidth, Constants.worldHeight, worldCamera).also {
            it.apply()
            worldCamera.setToOrtho(false, it.worldWidth, it.worldHeight)
        }

    lateinit var player: Player
    private var playerInputProcessor: PlayerInputProcessor

    private var grapevine: Grapevine
    private var hasGrapevine = false

    private val worldCameraController = WorldCameraController(worldCamera, worldViewport)

    private val gameMap = GameMap()
    private var mapSize: Vector2
    private val collisionSystem = CollisionSystem()
    private val npcController = NpcController(collisionSystem)
    private val interactionSystem = InteractionSystem()
    private val stonesList = mutableListOf<BreakableStone>()
    private val particleSystem = FruitParticleSystem()
    private val groundHolesManager: GroundHolesManager

    private val shapeRenderer = ShapeRenderer()

    private var font: BitmapFont

    private val droppedItems = mutableListOf<DroppedItem>()

    private val mapTransitionController: MapTransitionController

    init {

        val texture = Texture("player_sheet.png")
        val walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.wav"))

        grapevine = Grapevine(Vector2(300f, 50f))
        grapevine.onInteract = {
            val grapevinePosition = grapevine.getCollisionBounds()
            val position = grapevinePosition.getCenter(Vector2())
            particleSystem.spawn(
                position.add(0f, 48f),
                5,
                grapevine.fruitTexture
            )

            particleSystem.onComplete = {

                val item = DroppedItem(
                    grapevine.fruitTexture,
                    grapevinePosition.x - 20f,
                    grapevinePosition.y,
                    "grape"
                )
                droppedItems.add(item)

                val box = Rectangle(
                    item.x,
                    item.y,
                    grapevine.fruitTexture.regionWidth.toFloat(),
                    grapevine.fruitTexture.regionHeight.toFloat()
                )

                collisionSystem.addCollisionBox(item.itemId, box)

                interactionSystem.addInteractable(item.itemId, InteractableType.DroppedItem, box, {
                    player.itemInventory.addItem(
                        Item(
                            item.itemId,
                            item.itemId,
                            grapevine.fruitTexture
                        )
                    )
                    droppedItems.remove(item)
                    collisionSystem.removeCollisionBox(box)
                    interactionSystem.removeInteractable(box)
                })
            }
        }

        val mapName = "maps/main_house_outdoor_big.tmx"

        mapSize = gameMap.initMap(
            mapName,
            beforePlayerLayers = arrayOf("ground", "trees", "houseBase", "fountain"),
            afterPlayerLayers = arrayOf("abovePlayer"),
            spawnPointName = "spawn_path",
        )

        initializeMap(mapName)

        groundHolesManager = GroundHolesManager(interactionSystem.getGroundInteractables())

        player = Player(texture, walkSound, collisionSystem)

        worldCamera.position.set(
            player.x + player.texture.width / 2f,
            player.y + player.texture.height / 2f,
            0f
        )
        worldCamera.update()

        playerInputProcessor = PlayerInputProcessor(
            player,
            playerMovement = { delta, dx, dy ->
                player.update(delta, dx, dy)
            },
            onInteract = {
                interactionSystem
                    .handleInteraction(
                        player.getInteractionBounds(),
                        onNextMap =
                            { mapFilePath, beforePlayerLayers, afterPlayerLayers ->
                                mapSize = gameMap.loadNewMap(
                                    mapFilePath,
                                    beforePlayerLayers,
                                    afterPlayerLayers,
                                    spawnPointName = "spawn_my_door",
                                    onLoadedNewMap = { spawnPointName, targetMap ->
                                        spawnPlayer(spawnPointName)
                                        initializeMap(targetMap)
                                    }
                                )
                            },

                        onNextDay = {
                            clock.incrementDay()
                            spawnPlayer("spawn_bed")
                        }
                    )
            },
            onUsingTool = {
                val target = player.useTool(getWorldTargets(), playerData.energy)
                playerData.useEnergy(5f)
                when (target) {
                    is InteractableObject.StoneInteractable -> {
                        target.onInteract = {
                            interactionSystem.removeInteractable(target.bounds)
                            collisionSystem.removeCollisionBox(target.name)
                            gameMap.removeStoneLayer(target)
                            stonesList.firstOrNull { it.name == target.name }?.breakStone()
                        }
                    }

                    is InteractableObject.GroundInteractable -> {
                        target.onInteract = {
                            //TODO: remove interactable if digged enough,
                            //TODO: change it to bucket tool if it's raining
                            groundHolesManager.dig(target)
                            collisionSystem.addCollisionBox(target.name, target.bounds)
                            //TODO: animation
                        }
                    }
                }
            })

        Gdx.input.inputProcessor = playerInputProcessor
        spawnPlayer("spawn_path")

        font = BitmapFont()
        font.color = Color.WHITE

        clock.onNextDay = { _: Int, dayInYear: Int ->
            grapevine.checkGrowth(dayInYear)
        }

        npcController.onInteract = { person ->
            dialogManager.onInteract(person)
        }

        mapTransitionController = MapTransitionController(gameMap)
    }

    private fun initializeMap(mapName: String) {
        initSystems()
        checkAddGrapevineToSystems(mapName)
        hasGrapevine = mapHasGrapevine()
    }

    private fun getWorldTargets(): List<ToolTarget> {
        val list = mutableListOf<ToolTarget>()
        if (mapHasGrapevine()) list.add(grapevine)

        list.addAll(interactionSystem.getToolTargets())

        return list
    }

    private fun checkAddGrapevineToSystems(mapFilePath: String) {
        if (mapFilePath.contains("maps/main_house_outdoor_big.tmx")) {
            collisionSystem.addCollisionBox("grapevine", grapevine.getCollisionBounds())

            interactionSystem.addInteractable(
                "grapevine",
                InteractableType.Tree,
                grapevine.getCollisionBounds()
            ) {
                grapevine.onInteract()
            }
        }
    }

    private fun mapHasGrapevine(): Boolean {
        return gameMap.map.layers.map { it.name }.contains("grapevine")
    }

    fun render(delta: Float) {

        playerInputProcessor.update(delta, Constants.speed)
        val interactionBounds = player.getInteractionBounds()

        worldCameraController.update(
            delta,
            player.getCameraFocusX(),
            player.getCameraFocusY(),
            mapSize.x,
            mapSize.y
        )

        val interactableObject = interactionSystem.getNearbyInteraction(interactionBounds)
        particleSystem.update(delta)
        stonesList.forEach { it.update(delta) }
        npcController.update(delta)
        groundHolesManager.update(delta, clock.currentWeather)

        gameMap.renderMapBeforePlayer(worldCamera)

        batch.projectionMatrix = worldCamera.combined
        batch.begin()
        if (hasGrapevine) {
            grapevine.drawBehindPlayer(batch)
        }
        particleSystem.drawBehindPlayer(batch, player.y)
        droppedItems.forEach { it.render(batch) }
        stonesList.forEach { it.render(batch) }
        groundHolesManager.render(batch)
        npcController.render(batch)
        player.draw(batch)
        batch.end()

        gameMap.renderMapAfterPlayer(worldCamera)

        batch.begin()
        if (hasGrapevine) {
            grapevine.drawAbovePlayer(batch)
        }
        particleSystem.drawAbovePlayer(batch, player.y)
        interactionSystem.handleInteractionHint(
            interactionBounds,
            interactableObject,
            batch,
            font,
            delta
        )

        weatherSystem.updateAndRender(delta, batch, clock.currentWeather)
        batch.end()

        val newMapSize =
            mapTransitionController.update(player.getInteractionBounds()) { spawnPointName, targetMap ->
                spawnPlayer(spawnPointName)
                initializeMap(targetMap)
            }

        newMapSize?.let { mapSize = it }

        shapeRenderer.projectionMatrix = worldCamera.combined
        collisionSystem.drawDebug(shapeRenderer)
        interactionSystem.drawDebug(shapeRenderer)
        player.drawDebug(shapeRenderer)
        mapTransitionController.drawDebug(shapeRenderer)
    }

    private fun initSystems() {
        collisionSystem.initMap(gameMap.map)
        interactionSystem.initMap(gameMap.map)
        initWorldTargets()

        npcController.initMap(gameMap.map)
        npcController.initCollision(collisionSystem)
        npcController.initInteraction(interactionSystem)
    }

    private fun initWorldTargets() {
        getWorldTargets().forEach {
            if (it is InteractableObject.StoneInteractable) {
                stonesList.add(BreakableStone(it.name, it.bounds))
            }
        }
    }


    private fun spawnPlayer(spawnPoint: String) {
        gameMap.getSpawnPoint(spawnPoint).run {
            player.setPosition(location.x, location.y)
            player.setDirection(direction)
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
        grapevine.dispose()
    }
}
