package com.mojtabarahimy.frostpeak.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.util.Constants

class GameMap(
    mapFilePath: String,
    private val beforePlayerLayers : Array<String>,
    private val afterPlayerLayers: Array<String>,
    unitScale: Float = Constants.MAP_UNTI_SCALE
) {

    val map: TiledMap = TmxMapLoader().load(mapFilePath)
    private val renderer = OrthogonalTiledMapRenderer(map, unitScale)


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
}

private fun Array<String>.toMapIndices(map: TiledMap): IntArray {
    return mapNotNull { name -> map.layers.getIndex(name) }.toIntArray()
}
