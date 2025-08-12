package com.mojtabarahimy.frostpeak.util

object Constants {

    const val worldWidth = 800f
    const val worldHeight = 480f
    const val speed = 100f

    const val marginFraction: Float = 1f / 3f

    const val PLAYER_WIDTH = 32
    const val PLAYER_HEIGHT = 48

    const val PLAYER_WALK_FRAME_DURATION = 0.2f

    const val TILED_MAP_DEFAULT_TILE_SIZE = 4f

    const val MAP_UNTI_SCALE = 1f

    val ALL_MAP_LAYERS_BEHIND = arrayOf("ground", "trees", "houseBase", "fountain", "bed")
    val ALL_MAP_LAYERS_ABOVE = arrayOf("abovePlayer")
}
