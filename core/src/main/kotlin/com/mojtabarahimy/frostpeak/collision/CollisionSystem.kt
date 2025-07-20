package com.mojtabarahimy.frostpeak.collision

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.util.Constants

class CollisionSystem(map: TiledMap) {

    private val colliders: List<Rectangle>

    init {
        val temp = mutableListOf<Rectangle>()

        // Try to find any TiledMapTileLayer to grab tile size
        val sampleLayer = map.layers.firstOrNull { it is TiledMapTileLayer } as? TiledMapTileLayer
        val tileWidth = sampleLayer?.tileWidth?.toFloat() ?: Constants.TILED_MAP_DEFAULT_TILE_SIZE
        val tileHeight = sampleLayer?.tileHeight?.toFloat() ?: Constants.TILED_MAP_DEFAULT_TILE_SIZE

        Gdx.app.log("Frostpeak", "CollisionSystem: tileWidth:$tileWidth, tileHeight:$tileHeight")
        val collisionLayer = map.layers.get("collisions") as MapLayer

        for (mapObject in collisionLayer.objects) {
            if (mapObject is RectangleMapObject) {
                temp.add(mapObject.rectangle)
            }
        }

        colliders = temp
    }

    fun checkCollision(rect: Rectangle): Boolean {
        return colliders.any { it.overlaps(rect) }
    }

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.MAGENTA

        for (rect in colliders){
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
        }

        shapeRenderer.end()
    }
}
