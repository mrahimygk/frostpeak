package com.mojtabarahimy.frostpeak.interaction

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

data class InteractableObject(val name: String, val bounds: Rectangle)

class InteractionSystem(map: TiledMap) {
    private val interactables = mutableListOf<InteractableObject>()

    init {
        val objectLayer = map.layers.get("objects")

        for (mapObject in objectLayer.objects) {
            if (mapObject is RectangleMapObject) {
                interactables.add(
                    InteractableObject(mapObject.name, mapObject.rectangle)
                )
            }

            //TODO: add other interactables (i.e CircleMapObjects
        }
    }

    fun handleInteraction(playerBounds: Rectangle) {
        getNearbyInteraction(playerBounds)?.let {
            Gdx.app.log("Frostpeak", "interactionSystem: getNearbyInteraction:${it.name}")
        }
    }

    private fun getNearbyInteraction(playerBounds: Rectangle): InteractableObject? {
        return interactables.find { it.bounds.overlaps(playerBounds) }
    }
}
