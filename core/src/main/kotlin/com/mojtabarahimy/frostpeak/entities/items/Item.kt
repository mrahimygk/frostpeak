package com.mojtabarahimy.frostpeak.entities.items

import com.badlogic.gdx.graphics.g2d.TextureRegion

data class Item(
    val id: String,
    val name: String,
    val icon: TextureRegion
)
