package com.mojtabarahimy.frostpeak.time

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class GameClock {

    var hours = 6
    var minutes = 0

    private var timeAccumulator = 0f
    private val minutesPerSecond = 1

    var isPaused = false

    fun update(delta: Float) {
        if (isPaused) return

        timeAccumulator += delta

        while (timeAccumulator >= 1f) {
            addMinutes(minutesPerSecond)
            timeAccumulator -= 1f
        }
    }

    private fun addMinutes(minutes: Int) {
        this.minutes += minutes

        if (this.minutes >= 60) {
            this.minutes -= 60
            hours = (hours + 1) % 24
        }
    }

    fun getTimeString(): String {
        return String.format("%02d:%02d", hours, minutes)
    }

    fun draw(batch: SpriteBatch, font: BitmapFont) {
        font.draw(batch, "Time: ${getTimeString()}", 20f, 20f)
    }
}
