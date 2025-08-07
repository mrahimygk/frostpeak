package com.mojtabarahimy.frostpeak.controller.dialog

import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.Talkable
import com.mojtabarahimy.frostpeak.render.DialogRenderer

class DialogManager(
    private val dialogStore: DialogStore,
    private val dialogRenderer: DialogRenderer,
    private val gameClock: GameClock
) {

    fun onInteract(talkable: Talkable) {
        getDialog(
            talkable,
            gameClock.season,
            gameClock.day,
        )?.let {
            dialogRenderer.startDialog(it)
        }

    }

    fun getDialog(
        talkable: Talkable,
        season: Season,
        day: Int,
    ): List<DialogLine>? {
        return dialogStore.dialogs.firstOrNull { it.npc == talkable.nameId }?.run {
            if (!talkable.hasIntroduced) {
                talkable.hasIntroduced = true
                dialogs["introduction"]
            } else {
                dialogs["${season.name.lowercase()}.day_${day}"]
            }
        }
    }
}
