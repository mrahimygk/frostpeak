package com.mojtabarahimy.frostpeak.entities.crops

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2

class Grapevine(
    position: Vector2,
    daysForNextStage: Int = 2
) : Tree(position, daysForNextStage) {

    override val atlas: TextureAtlas
        get() = TextureAtlas(Gdx.files.internal("crops/grapevine/grapevine_small_atlas.atlas"))

}
