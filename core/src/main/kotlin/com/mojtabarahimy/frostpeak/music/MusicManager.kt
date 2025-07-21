package com.mojtabarahimy.frostpeak.music

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music

object MusicManager {
    private var currentMusic: Music? = null

    fun playMusic(filePath: String) {
        //if (currentMusic?.isPlaying == true && currentMusic?.file?.path() == filePath) return

        currentMusic?.stop()
        currentMusic?.dispose()

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath)).apply {
            isLooping = true
            volume = 0.4f
            play()
        }
    }

    fun stop() {
        currentMusic?.stop()
    }

    fun dispose() {
        currentMusic?.dispose()
    }
}
