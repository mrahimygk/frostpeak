package com.mojtabarahimy.frostpeak.controller.npc

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.entities.Person
import com.mojtabarahimy.frostpeak.entities.npc.Georgiana
import com.mojtabarahimy.frostpeak.interaction.InteractableType
import com.mojtabarahimy.frostpeak.interaction.InteractionSystem

class NpcController(collisionSystem: CollisionSystem) {

    private val npcs = mutableListOf<Person>()
    var onInteract: ((Person) -> Boolean)? = null

    private val georgianaTexture = Texture("npc/sprites/georgiana.png")

    private val georgiana = Georgiana(
        georgianaTexture,
        Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.wav")),
        collisionSystem
    )

    fun initMap(map: TiledMap) {
        npcs.clear()

        val npcLayer = map.layers.get("npc") ?: return
        for (obj in npcLayer.objects) {
            if (obj is RectangleMapObject) {
                georgiana.setPosition(obj.rectangle.x, obj.rectangle.y)
            }
        }

        npcs.add(georgiana)

    }

    fun update(delta: Float) {
        npcs.forEach {
            it.update(delta, 0f, 0f)
        }
    }

    fun render(batch: SpriteBatch) {
        npcs.forEach {
            it.draw(batch)
        }
    }

    fun initCollision(collisionSystem: CollisionSystem) {
        npcs.forEach {
            collisionSystem.addCollisionBox(
                it.javaClass.simpleName,
                Rectangle(it.x, it.y, 20f, 20f)
            )
        }
    }

    fun initInteraction(interactionSystem: InteractionSystem) {
        npcs.forEach {
            interactionSystem.addInteractable(
                it.javaClass.simpleName,
                InteractableType.Npc,
                Rectangle(it.x, it.y, 20f, 20f),
                onInteract = {
                    onInteract?.invoke(it) ?: false
                }
            )
        }
    }
}
