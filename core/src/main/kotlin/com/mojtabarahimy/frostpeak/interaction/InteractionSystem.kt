package com.mojtabarahimy.frostpeak.interaction

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Rectangle

data class InteractableObject(val name: String, val bounds: Rectangle)

class InteractionSystem {
    private lateinit var interactables: List<InteractableObject>
    private var stateTime = 0f

    fun initMap(map: TiledMap) {
        val objectLayer = map.layers.get("objects")
        val temp = mutableListOf<InteractableObject>()

        for (mapObject in objectLayer.objects) {
            if (mapObject is RectangleMapObject) {
                temp.add(
                    InteractableObject(mapObject.name, mapObject.rectangle)
                )
            }

            //TODO: add other interactables (i.e CircleMapObjects
        }

        interactables = temp
    }

    fun handleInteraction(
        playerBounds: Rectangle,
        onNextMap: (
            mapFilePath: String,
            beforePlayerLayers: Array<String>,
            afterPlayerLayers: Array<String>
        ) -> Unit
    ) {
        getNearbyInteraction(playerBounds)?.let {
            Gdx.app.log("Frostpeak", "interactionSystem: getNearbyInteraction:${it.name}")
            when (it.name) {
                "house_door" -> onNextMap(
                    "maps/main_house_indoor.tmx",
                    arrayOf("ground", "bed"),
                    arrayOf("abovePlayer"),
                )

                "exit_door" -> onNextMap(
                    "maps/main_house_outdoor.tmx",
                    arrayOf("ground", "trees", "houseBase"),
                    arrayOf("abovePlayer"),
                )

                "mullberry_tree_main" -> {

                }

                "bed" -> {

                }
            }
        }
    }

    fun getNearbyInteraction(playerBounds: Rectangle): InteractableObject? {
        return interactables.find { it.bounds.overlaps(playerBounds) }
    }

    fun handleInteractionHint(
        playerBounds: Rectangle,
        interactableObject: InteractableObject?,
        batch: SpriteBatch,
        font: BitmapFont,
        delta: Float
    ) {
        stateTime += delta
        interactableObject?.run {
            val layout = GlyphLayout(font, "Press E")
            val offsetX = -layout.width / 2
            val baseY = 50f
            val floatOffset = (sin(stateTime * 2f) * 5f)

            font.draw(batch, layout, playerBounds.x + offsetX, playerBounds.y + baseY + floatOffset)
        }
    }
}
