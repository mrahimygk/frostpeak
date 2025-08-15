package com.mojtabarahimy.frostpeak.controller.npc

import com.badlogic.gdx.math.Vector2
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.Direction

data class NpcSchedule(
    val year: Int,
    val season: Season,
    val day: Int,
    val hours: Int,
    val minutes: Int,
    val target: Vector2,
    val action: NpcAction,
    val facing: Direction
)
