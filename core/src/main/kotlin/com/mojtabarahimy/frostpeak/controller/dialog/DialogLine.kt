package com.mojtabarahimy.frostpeak.controller.dialog

import com.badlogic.gdx.graphics.g2d.TextureRegion

data class DialogLine(
    val text: String,
    val portrait: TextureRegion? = null  // null for narration
)
