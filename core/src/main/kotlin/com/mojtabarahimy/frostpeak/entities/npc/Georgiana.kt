package com.mojtabarahimy.frostpeak.entities.npc

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.mojtabarahimy.frostpeak.collision.CollisionSystem
import com.mojtabarahimy.frostpeak.data.NpcNamesList
import com.mojtabarahimy.frostpeak.entities.Person

class Georgiana(
    val texture: Texture,
    val walkSound: Sound,
    private val collisionSystem: CollisionSystem
) : Person by Npc(texture, walkSound, collisionSystem) {

    override val name: String
        get() = NpcNamesList.Georgiana.name.lowercase()

    override val nameId: String
        get() = name
}
