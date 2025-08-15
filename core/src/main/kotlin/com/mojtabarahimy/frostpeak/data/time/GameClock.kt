package com.mojtabarahimy.frostpeak.data.time

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.mojtabarahimy.frostpeak.data.WeatherType
import com.mojtabarahimy.frostpeak.util.Constants
import java.util.Locale

val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")


class GameClock(
    var currentWeather: WeatherType = WeatherType.SUNNY,
    var onMinuteTick: ((clock: GameClock) -> Unit)? = null,
    var onHourTick: ((clock: GameClock) -> Unit)? = null,
    var onNextDay: ((clock: GameClock) -> Unit)? = null,
    var onNextWeek: ((clock: GameClock) -> Unit)? = null,
    var onNextSeason: ((clock: GameClock) -> Unit)? = null,
    var onNextYear: ((clock: GameClock) -> Unit)? = null,
) {

    var hours = 6
    var minutes = 0
    var day = 1
    var dayCounter = 0
    var dayInYear: Int = dayCounter
        get() = dayCounter % 120
        private set

    var season = Season.SPRING
    var year = 1

    private var timeAccumulator = 0f
    private val minutesPerSecond = 1

    var isPaused = false

    val dayOfWeek: String
        get() = daysOfWeek[(getDayIndex() % 7)]

    private var nextWeatherChangeHour = 10

    fun update(delta: Float) {
        if (isPaused) return

        timeAccumulator += delta

        while (timeAccumulator >= 10f) {
            addMinutes(minutesPerSecond * 10)
            timeAccumulator -= 10f
        }

        if (hours >= nextWeatherChangeHour) {
            changeWeather()
        }
    }

    private fun addMinutes(minutes: Int) {
        this.minutes += minutes

        if (this.minutes >= 60) {
            this.minutes = 0
            hours += 1
            onHourTick?.invoke(this)
        }

        if (hours >= 24) {
            incrementDay()
        }

        onMinuteTick?.invoke(this)
    }

    fun incrementDay() {
        hours = 6
        minutes = 0
        day += 1
        dayCounter += 1
        onNextDay?.invoke(this)
        if (dayCounter % 7 == 0) onNextWeek?.invoke(this)
        if (day > 30) {
            day = 1
            season = Season.entries.toTypedArray()[(season.ordinal + 1) % 4]
            onNextSeason?.invoke(this)
            if (season == Season.SPRING) {
                year += 1
                onNextYear?.invoke(this)
            }
        }
    }

    private fun changeWeather() {
        println("Weather is changing")
        currentWeather = if (MathUtils.randomBoolean(0.3f)) {
            if (currentWeather == WeatherType.SUNNY) WeatherType.RAINY else WeatherType.SUNNY
        } else {
            currentWeather
        }

        println("weather changed to $currentWeather")
        nextWeatherChangeHour = hours + MathUtils.random(3, 6)
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
