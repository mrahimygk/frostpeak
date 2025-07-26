package com.mojtabarahimy.frostpeak.controller

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.mojtabarahimy.frostpeak.util.Constants

class WorldCameraController(
    private val camera: OrthographicCamera,
    private val viewport: Viewport,
    private val lerpSpeed: Float = 5f
) {
    private val targetPos = Vector2(camera.position.x, camera.position.y)

    fun update(delta: Float, focusX: Float, focusY: Float, mapWidth: Float, mapHeight: Float) {
        val safeMarginX = viewport.worldWidth * Constants.marginFraction
        val safeMarginY = viewport.worldHeight * Constants.marginFraction

        val camLeft = camera.position.x - viewport.worldWidth / 2f + safeMarginX
        val camRight = camera.position.x + viewport.worldWidth / 2f - safeMarginX
        val camBottom = camera.position.y - viewport.worldHeight / 2f + safeMarginY
        val camTop = camera.position.y + viewport.worldHeight / 2f - safeMarginY

        // Determine target position
        targetPos.set(camera.position.x, camera.position.y)

        if (focusX < camLeft) {
            targetPos.x = focusX - safeMarginX + viewport.worldWidth / 2f
        } else if (focusX > camRight) {
            targetPos.x = focusX + safeMarginX - viewport.worldWidth / 2f
        }

        if (focusY < camBottom) {
            targetPos.y = focusY - safeMarginY + viewport.worldHeight / 2f
        } else if (focusY > camTop) {
            targetPos.y = focusY + safeMarginY - viewport.worldHeight / 2f
        }

        // Lerp the camera to the target
        camera.position.x += (targetPos.x - camera.position.x) * lerpSpeed * delta
        camera.position.y += (targetPos.y - camera.position.y) * lerpSpeed * delta

        val halfWidth = viewport.worldWidth / 2f
        val halfHeight = viewport.worldHeight / 2f

        camera.position.x = if (mapWidth <= viewport.worldWidth) mapWidth / 2f
        else camera.position.x.coerceIn(halfWidth, mapWidth - halfWidth)
        camera.position.y = if (mapHeight <= viewport.worldHeight) mapHeight / 2f + 32f
        else camera.position.y.coerceIn(halfHeight, mapHeight - halfHeight)

        camera.update()
    }
}
