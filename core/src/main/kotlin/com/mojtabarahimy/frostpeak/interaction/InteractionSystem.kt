package com.mojtabarahimy.frostpeak.interaction

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Rectangle

data class InteractableObject(
    val name: String,
    val bounds: Rectangle,
    val onInteract: (() -> Unit)? = null
)

class InteractionSystem {
    private val interactables = mutableListOf<InteractableObject>()
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

        interactables.clear()
        interactables.addAll(temp)
    }

    fun handleInteraction(
        playerBounds: Rectangle,
        onNextMap: (
            mapFilePath: String,
            beforePlayerLayers: Array<String>,
            afterPlayerLayers: Array<String>
        ) -> Unit,
        onNextDay: () -> Unit
    ) {
        getNearbyInteraction(playerBounds)?.let {
            Gdx.app.log("Frostpeak", "interactionSystem: getNearbyInteraction:${it.name}")

            //TODO: if invoke() returns false, we do not return@handleInteraction, and we let the code continue
            it.onInteract?.let {
                it.invoke()
                return
            }

            when (it.name) {
                "house_door" -> onNextMap(
                    "maps/main_house_indoor.tmx",
                    arrayOf("ground", "bed"),
                    arrayOf("abovePlayer"),
                )

                "exit_door" -> onNextMap(
                    "maps/main_house_outdoor_big.tmx",
                    arrayOf("ground", "trees", "houseBase"),
                    arrayOf("abovePlayer"),
                )

                "mullberry_tree_main" -> {

                }

                "bed" -> {
                    onNextDay()
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

    fun addInteractable(name: String, interactable: Rectangle, onInteract: (() -> Unit)) {
        interactables.add(InteractableObject(name, interactable, onInteract))
    }

    fun removeInteractable(interactable: Rectangle) {
        interactables.removeAt(interactables.indexOfFirst { it.bounds == interactable })
    }

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GREEN

        interactables.forEach {
            val rect = it.bounds
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
        }

        shapeRenderer.end()
    }
}
