package com.mojtabarahimy.frostpeak.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mojtabarahimy.frostpeak.controller.dialog.DialogLine

class DialogRenderer(
    private val font: BitmapFont,
    private val dialogBox: NinePatch
) {
    var isActive = false
        private set

    private var dialogQueue = mutableListOf<DialogLine>()
    private lateinit var currentPage: DialogLine
    private var displayedText = ""
    private var charIndex = 0
    private var timer = 0f
    private var typingSpeed = 0.05f
    private var isTyping = false
    private var isSkipping = false

    fun startDialog(pages: List<DialogLine>) {
        dialogQueue.clear()
        dialogQueue.addAll(pages)
        isActive = true
        nextPage()
    }

    fun update(delta: Float) {
        if (!isActive || !isTyping) return

        timer += delta
        val speed = if (isSkipping) typingSpeed / 4f else typingSpeed
        while (timer >= speed && charIndex < currentPage.text.length) {
            timer -= speed
            displayedText += currentPage.text[charIndex]
            charIndex++
        }

        if (charIndex >= currentPage.text.length) {
            isTyping = false
        }
    }

    fun draw(batch: SpriteBatch, screenWidth: Float, screenHeight: Float) {
        if (!isActive) return

        val frameHeight = 120f
        val dialogY = -screenHeight / 2f// + dialogBox.texture.height.toFloat()
        val dialogX = -screenWidth / 2f
        val padding = 20f

        dialogBox.draw(batch, dialogX, dialogY, screenWidth, frameHeight)
        currentPage.portrait?.let {
            batch.draw(it, dialogX, dialogY)
        }
        font.draw(batch, displayedText, dialogX + padding, dialogY + frameHeight - padding)
    }

    fun handleInput() {
        if (!isActive) return

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.N) -> {
                if (isTyping) {
                    displayedText = currentPage.text
                    charIndex = currentPage.text.length
                    isTyping = false
                } else {
                    nextPage()
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.C) -> {
                endDialog()
            }

            Gdx.input.isKeyJustPressed(Input.Keys.L) -> {
                isSkipping = true
            }
        }

        // L key up stops skipping
        if (!Gdx.input.isKeyPressed(Input.Keys.L)) {
            isSkipping = false
        }
    }

    private fun nextPage() {
        if (dialogQueue.isEmpty()) {
            endDialog()
            return
        }

        currentPage = dialogQueue.removeAt(0)
        displayedText = ""
        charIndex = 0
        timer = 0f
        isTyping = true
    }

    fun endDialog() {
        isActive = false
        dialogQueue.clear()
        displayedText = ""
        charIndex = 0
    }
}
