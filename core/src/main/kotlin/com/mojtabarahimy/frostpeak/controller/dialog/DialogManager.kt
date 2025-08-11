package com.mojtabarahimy.frostpeak.controller.dialog

import com.mojtabarahimy.frostpeak.controller.quests.QuestManager
import com.mojtabarahimy.frostpeak.data.quests.QuestDefinition
import com.mojtabarahimy.frostpeak.data.quests.QuestStatus
import com.mojtabarahimy.frostpeak.data.time.GameClock
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.Talkable
import com.mojtabarahimy.frostpeak.render.DialogRenderer

class DialogManager(
    private val dialogStore: DialogStore,
    val dialogRenderer: DialogRenderer,
    private val gameClock: GameClock,
    private val questManager: QuestManager
) {

    fun onInteract(
        talkable: Talkable,
        onStartQuest: (quest: QuestDefinition) -> Unit
    ) : Boolean {
        getDialog(
            talkable,
            gameClock.year,
            gameClock.season,
            gameClock.day,
            onStartQuest
        )?.let {
            dialogRenderer.startDialog(it)
            return true
        }

        return false
    }

    private fun getDialog(
        talkable: Talkable,
        year: Int,
        season: Season,
        day: Int,
        onStartQuest: (quest: QuestDefinition) -> Unit
    ): List<DialogLine>? {

        val quests: List<QuestDefinition> = questManager
            .getAvailableQuests(talkable, year, season, day)

        val hasQuestsForMe = quests.any {
            it.state.status == QuestStatus.NOT_STARTED ||
                it.state.status == QuestStatus.IN_PROGRESS
        }
        val dialogs = dialogStore.dialogs.firstOrNull { it.npc == talkable.nameId }

        var currentDay = day

        if (!talkable.hasIntroduced) {
            dialogs?.let {
                talkable.hasIntroduced = true
                return it.dialogs["introduction"]
            }
        } else {
            if (hasQuestsForMe) {
                quests.firstOrNull()?.let {
                    val questDialogs = it.questDialogs[it.state.status]
                    if (it.state.status == QuestStatus.NOT_STARTED) {
                        questManager.startQuest(it.questId)
                        onStartQuest.invoke(it)
                    } else {
                        //TODO: check if meet the quest requirements
                        questManager.completeQuest(it.questId)
                    }
                    return questDialogs
                }
            }
            while (currentDay >= 1) {
                val key = "${season.name.lowercase()}.day_$currentDay"
                dialogs?.dialogs?.get(key)?.let { return it }
                currentDay--
            }
        }

        return null
    }

    fun startPlayerDialog(noBucket: PlayerDialogs) {
        dialogStore.getPlayerDialog(noBucket)?.let {
            dialogRenderer.startDialog(it)
        }
    }
}
