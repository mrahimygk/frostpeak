package com.mojtabarahimy.frostpeak.entities.npc

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.mojtabarahimy.frostpeak.data.NpcNamesList
import com.mojtabarahimy.frostpeak.entities.Person

class Georgiana(
    val texture: Texture,
    val walkSound: Sound,
) : Person by Npc(NpcNamesList.Georgiana.name.lowercase(), texture, walkSound)
