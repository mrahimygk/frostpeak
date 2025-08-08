package com.mojtabarahimy.frostpeak.data

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mojtabarahimy.frostpeak.interaction.InteractableObject

class GroundHolesManager(private val holeList: List<InteractableObject.GroundInteractable>) {

    private val shallowHoleTexture = Texture("maps/ground/shallow_hole.png")
    private val deepHoleTexture = Texture("maps/ground/deep_hole.png")

    fun dig(groundInteractable: InteractableObject.GroundInteractable): Boolean {
        groundInteractable.dig()
        return groundInteractable.digLevel > 0
    }

    fun render(batch: SpriteBatch) {
        holeList.forEach { hole ->
            hole.bounds.run {
                when (hole.digLevel) {
                    0 -> return
                    1 -> batch.draw(shallowHoleTexture, x, y )
                    2 -> batch.draw(deepHoleTexture, x , y )
                    else -> TODO()
                }
            }
        }
    }


}
