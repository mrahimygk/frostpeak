package com.mojtabarahimy.frostpeak.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.util.Constants

class GameMap {

    lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private lateinit var beforePlayerLayers: Array<String>
    private lateinit var afterPlayerLayers: Array<String>

    fun initMap(
        mapFilePath: String,
        beforePlayerLayers: Array<String>,
        afterPlayerLayers: Array<String>,
        unitScale: Float = Constants.MAP_UNTI_SCALE
    ) : Vector2 {
        map = TmxMapLoader().load(mapFilePath)
        renderer = OrthogonalTiledMapRenderer(map, unitScale)
        this.beforePlayerLayers = beforePlayerLayers
        this.afterPlayerLayers = afterPlayerLayers

        val musicPath = map.properties["music"] as? String
        //TODO: musicPath?.let { MusicManager.playMusic(it) }

        return getMapSize()
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

    fun getSpawnPoint(): Rectangle {

        val objects = map.layers.get("objects").objects
        val spawn: MapObject = objects.get("player_spawn")
        val x: Float = spawn.properties["x"] as Float
        val y: Float = spawn.properties["y"] as Float

        return Rectangle(x, y, 1f, 1f)
    }


    fun renderMapBeforePlayer(camera: OrthographicCamera) {
        renderer.setView(camera)
        renderer.render(beforePlayerLayers.toMapIndices(map))
    }

    fun renderMapAfterPlayer(camera: OrthographicCamera) {
        renderer.setView(camera)
        renderer.render(afterPlayerLayers.toMapIndices(map))
    }

    fun dispose() {
        map.dispose()
        renderer.dispose()
    }

    fun loadNewMap(
        mapFilePath: String,
        beforePlayerLayers: Array<String>,
        afterPlayerLayers: Array<String>,
        unitScale: Float = Constants.MAP_UNTI_SCALE
    ) : Vector2 {
        dispose()
        return initMap(
            mapFilePath,
            beforePlayerLayers,
            afterPlayerLayers,
            unitScale,
        )

    }
}

private fun Array<String>.toMapIndices(map: TiledMap): IntArray {
    return mapNotNull { name -> map.layers.getIndex(name) }.toIntArray()
}
