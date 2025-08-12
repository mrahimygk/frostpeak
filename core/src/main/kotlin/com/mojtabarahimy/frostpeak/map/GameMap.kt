package com.mojtabarahimy.frostpeak.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.MapGroupLayer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.entities.Direction
import com.mojtabarahimy.frostpeak.interaction.InteractableObject
import com.mojtabarahimy.frostpeak.util.Constants

class GameMap {

    lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private lateinit var stonesLayers: IntArray
    private val stonesLayerNames = mutableListOf<String>()
    private val spawnPoints: MutableMap<String, SpawnPoint> = mutableMapOf()

    fun initMap(
        mapFilePath: String,
        unitScale: Float = Constants.MAP_UNTI_SCALE,
        spawnPointName: String,
        onLoadedNewMap: ((spawnPointName: String, targetMap: String) -> Unit)? = null
    ): Vector2 {
        map = TmxMapLoader().load(mapFilePath)
        renderer = OrthogonalTiledMapRenderer(map, unitScale)
        val stoneLayerNamesTmpList = mutableListOf<String>()
        map.layers.get("stones")?.let { stonesGroup ->
            if (stonesGroup is MapGroupLayer) {
                stonesGroup.layers.forEach { stoneLayer ->
                    stoneLayerNamesTmpList.add(stoneLayer.name)
                }
            }
        }

        stonesLayerNames.clear()
        stonesLayerNames.addAll(stoneLayerNamesTmpList)
        recalculateStoneLayers()

        val musicPath = map.properties["music"] as? String
        //TODO: musicPath?.let { MusicManager.playMusic(it) }

        fillPlayerSpawnPoints()

        onLoadedNewMap?.invoke(spawnPointName, mapFilePath)
        return getMapSize()
    }

    private fun recalculateStoneLayers() {
        val stoneLayerIndicesTmpList = mutableListOf<Int>()
        stonesLayerNames.forEach {
            stoneLayerIndicesTmpList.add(findLayerIndex(map, it))
        }

        this.stonesLayers = stoneLayerIndicesTmpList.toIntArray()
    }

    private fun fillPlayerSpawnPoints() {
        val spawnPoints = mutableMapOf<String, SpawnPoint>()

        val objects = map.layers.get("SpawnPoints").objects
        for (obj in objects) {
            if (obj is RectangleMapObject) {
                val name = obj.name ?: continue
                val rect = obj.rectangle
                val direction =
                    Direction.valueOf(obj.properties.get("facing").toString().uppercase())
                spawnPoints[name] = SpawnPoint(direction, Vector2(rect.x, rect.y))
            }
        }

        this.spawnPoints.clear()
        this.spawnPoints.putAll(spawnPoints)

    }

    private fun getMapSize(): Vector2 {
        val mapWidth = map.properties.get("width", Int::class.java)
        val mapHeight = map.properties.get("height", Int::class.java)
        val tileWidth = map.properties.get("tilewidth", Int::class.java)
        val tileHeight = map.properties.get("tileheight", Int::class.java)

        val mapPixelWidth = mapWidth * tileWidth
        val mapPixelHeight = mapHeight * tileHeight
        return Vector2(mapPixelWidth.toFloat(), mapPixelHeight.toFloat())
    }

    fun getSpawnPoint(spawnPointName: String): SpawnPoint {
        return spawnPoints[spawnPointName] ?: SpawnPoint(Direction.DOWN, Vector2(0f, 0f))
    }

    fun getExitLayer(): MapLayer? {
        val exitsLayer = map.layers.get("exits")
        return exitsLayer
    }

    fun renderMapBeforePlayer(camera: OrthographicCamera) {
        renderer.setView(camera)
        renderer.render(Constants.ALL_MAP_LAYERS_BEHIND.toMapIndices(map))
        renderer.render(stonesLayers)
    }

    fun renderMapAfterPlayer(camera: OrthographicCamera) {
        renderer.setView(camera)
        renderer.render(Constants.ALL_MAP_LAYERS_ABOVE.toMapIndices(map))
    }

    fun dispose() {
        map.dispose()
        renderer.dispose()
    }

    fun loadNewMap(
        mapFilePath: String,
        unitScale: Float = Constants.MAP_UNTI_SCALE,
        spawnPointName: String,
        onLoadedNewMap: ((spawnPointName: String, targetMap: String) -> Unit)? = null
    ): Vector2 {
        dispose()
        return initMap(
            mapFilePath,
            unitScale,
            spawnPointName,
            onLoadedNewMap
        )
    }

    fun findLayerIndex(map: TiledMap, targetName: String): Int {
        var index = -1
        fun recurse(layers: MapLayers): Int {
            for (layer in layers) {
                if (layer.name == targetName) return index
                index++
                if (layer is MapGroupLayer) {
                    val found = recurse(layer.layers)
                    if (found != -1) return found
                }
            }
            return -1
        }
        return recurse(map.layers)
    }

    fun removeStoneLayer(target: InteractableObject.StoneInteractable) {
        stonesLayerNames.remove(target.name)
        recalculateStoneLayers()
    }
}

private fun Array<String>.toMapIndices(map: TiledMap): IntArray {
    return mapNotNull { name -> map.layers.getIndex(name) }.filter { it >= 0 }.toIntArray()
}
