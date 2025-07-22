package com.mojtabarahimy.frostpeak.time

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mojtabarahimy.frostpeak.util.Constants

enum class Season { SPRING, SUMMER, FALL, WINTER }

val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")


class GameClock {

    var hours = 6
    var minutes = 0
    var day = 1
    var season = Season.SPRING
    var year = 1

    private var timeAccumulator = 0f
    private val minutesPerSecond = 1

    var isPaused = false

    val dayOfWeek: String
        get() = daysOfWeek[(getDayIndex() % 7)]

    fun update(delta: Float) {
        if (isPaused) return

        timeAccumulator += delta

        while (timeAccumulator >= 10f) {
            addMinutes(minutesPerSecond * 10)
            timeAccumulator -= 10f
        }
    }

    private fun addMinutes(minutes: Int) {
        this.minutes += minutes

        if (this.minutes >= 60) {
            this.minutes = 0
            hours += 1
        }

        if (hours >= 24) {
            hours = 6
            incrementDay()
        }
    }

    private fun incrementDay() {
        day += 1
        if (day > 30) {
            day = 1
            season = Season.values()[(season.ordinal + 1) % 4]
            if (season == Season.SPRING) {
                year += 1
            }
        }
    }

    fun getTimeString(): String {
        return String.format("%02d:%02d", hours, minutes)
    }

    fun getFormattedDate(): String {
        return "$dayOfWeek $day ${season.name.capitalize()} Y$year"
    }

    private fun getDayIndex(): Int {
        val seasonOffset = season.ordinal * 30
        return (seasonOffset + day - 1)
    }

    fun draw(batch: SpriteBatch, font: BitmapFont) {
        font.draw(
            batch,
            "Time: ${getTimeString()}",
            Constants.worldWidth / 3f,
            Constants.worldHeight / 3f
        )

        font.draw(
            batch,
            "${getFormattedDate()}",
            Constants.worldWidth / 3f,
            Constants.worldHeight / 3f - 20f
        )
    }
}
