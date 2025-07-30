package com.mojtabarahimy.frostpeak.controller

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.map.GameMap

class MapTransitionController(private val gameMap: GameMap) {

    fun update(
        playerBounds: Rectangle,
        onLoadedNewMap: ((spawnPointName: String, targetMap: String) -> Unit)? = null
    ) {
        val exit = checkExitCollision(playerBounds)

        if (exit != null) {
            val targetMap = exit.properties["targetMap"] as? String
            val targetSpawn = exit.properties["targetSpawn"] as? String
            if (targetMap != null && targetSpawn != null) {
                gameMap.loadNewMap(
                    "maps/$targetMap",
                    arrayOf("ground"),
                    arrayOf("abovePlayer"),
                    spawnPointName = targetSpawn,
                    onLoadedNewMap = onLoadedNewMap
                )
            }
        }
    }

    private fun checkExitCollision(playerBounds: Rectangle): RectangleMapObject? {
        val objects = gameMap.getExitLayer()?.objects ?: return null
        for (mapObj in objects) {
            if (mapObj !is RectangleMapObject) continue

            val rect = mapObj.rectangle
            if (playerBounds.overlaps(rect)) {
                return mapObj
            }
        }

        return null
    }


    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.MAGENTA
        val objects = gameMap.getExitLayer()?.objects
        objects?.forEach { mapObj ->
            if (mapObj is RectangleMapObject) {

                val rect = mapObj.rectangle
                shapeRenderer.rect(
                    rect.x,
                    rect.y,
                    rect.width,
                    rect.height
                )
            }
        }


        shapeRenderer.end()
    }
}
