package com.mojtabarahimy.frostpeak.entities.crops

import com.badlogic.gdx.math.Vector2

interface Interactable {
    fun canInteract(playerPosition: Vector2): Boolean
    fun onInteract()
}
