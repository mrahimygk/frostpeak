package com.mojtabarahimy.frostpeak.collision

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.util.Constants

class CollisionSystem {

    private val colliders = mutableListOf<Collider>()

    fun initMap(map: TiledMap) {
        val temp = mutableListOf<Collider>()

        // Try to find any TiledMapTileLayer to grab tile size
        val sampleLayer = map.layers.firstOrNull { it is TiledMapTileLayer } as? TiledMapTileLayer
        val tileWidth = sampleLayer?.tileWidth?.toFloat() ?: Constants.TILED_MAP_DEFAULT_TILE_SIZE
        val tileHeight = sampleLayer?.tileHeight?.toFloat() ?: Constants.TILED_MAP_DEFAULT_TILE_SIZE

        Gdx.app.log("Frostpeak", "CollisionSystem: tileWidth:$tileWidth, tileHeight:$tileHeight")
        val collisionLayer = map.layers.get("collisions") as MapLayer

        for (mapObject in collisionLayer.objects) {
            when (mapObject) {
                is RectangleMapObject -> temp.add(Collider.Box(mapObject.rectangle))
                is PolygonMapObject -> {
                    val polygon = mapObject.polygon
                    val transformed = Polygon(polygon.transformedVertices)
                    temp.add(Collider.Poly(transformed))
                }
            }
        }

        colliders.clear()
        colliders.addAll(temp)
    }

    fun checkCollision(rect: Rectangle): Boolean {
        var collidesWithAny = false
        for (collider in colliders) {
            collidesWithAny = when (collider) {
                is Collider.Box -> collider.rect.overlaps(rect)
                is Collider.Poly -> Intersector.overlapConvexPolygons(
                    rect.toPolygon(),
                    collider.polygon
                )
            }
            if (collidesWithAny) break
        }

        return collidesWithAny
    }

    fun Rectangle.toPolygon(): Polygon {
        val rect: Rectangle = this
        val vertices = floatArrayOf(
            rect.x, rect.y,
            rect.x + rect.width, rect.y,
            rect.x + rect.width, rect.y + rect.height,
            rect.x, rect.y + rect.height
        )
        return Polygon(vertices)
    }

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.MAGENTA

        for (collider in colliders) {
            when (collider) {
                is Collider.Box -> shapeRenderer.rect(
                    collider.rect.x,
                    collider.rect.y,
                    collider.rect.width,
                    collider.rect.height
                )

                is Collider.Poly -> shapeRenderer.polygon(collider.polygon.transformedVertices)
            }
        }

        shapeRenderer.end()
    }

    fun addCollisionBox(collisionBounds: Rectangle) {
        colliders.add(Collider.Box(collisionBounds))
    }

    fun removeCollisionBox(collisionBounds: Rectangle) {
        colliders.removeAt(colliders.indexOfFirst { it is Collider.Box && it.rect == collisionBounds })
    }
}
