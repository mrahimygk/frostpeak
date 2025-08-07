package com.mojtabarahimy.frostpeak.data.time

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mojtabarahimy.frostpeak.util.Constants
import java.util.Locale

val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")


class GameClock(
    var onNextDay: ((dayInMonth: Int, dayInYear: Int) -> Unit)? = null,
    var onNextWeek: ((dayInYear: Int) -> Unit)? = null,
    var onNextSeason: ((dayInYear: Int, season: Season) -> Unit)? = null,
    var onNextYear: ((year: Int) -> Unit)? = null,
) {

    private var hours = 6
    private var minutes = 0
    var day = 1
    var dayCounter = 0
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
            incrementDay()
        }
    }

    fun incrementDay() {
        hours = 6
        minutes = 0
        day += 1
        dayCounter += 1
        onNextDay?.invoke(day, dayCounter)
        if (dayCounter % 7 == 0) onNextWeek?.invoke(dayCounter)
        if (day > 30) {
            day = 1
            season = Season.entries.toTypedArray()[(season.ordinal + 1) % 4]
            onNextSeason?.invoke(dayCounter, season)
            if (season == Season.SPRING) {
                year += 1
                onNextYear?.invoke(year)
            }
        }
    }

    private fun getTimeString(): String {
        return String.format(Locale.US, "%02d:%02d", hours, minutes)
    }

    private fun getFormattedDate(): String {
        return "$dayOfWeek $day ${
            season.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        } Y$year"
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
            getFormattedDate(),
            Constants.worldWidth / 3f,
            Constants.worldHeight / 3f - 20f
        )
    }
}
