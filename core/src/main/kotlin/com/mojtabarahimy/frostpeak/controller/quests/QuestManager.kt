package com.mojtabarahimy.frostpeak.controller.quests;

import com.mojtabarahimy.frostpeak.data.quests.QuestDefinition
import com.mojtabarahimy.frostpeak.data.quests.QuestState
import com.mojtabarahimy.frostpeak.data.quests.QuestStatus
import com.mojtabarahimy.frostpeak.data.time.Season
import com.mojtabarahimy.frostpeak.entities.Talkable

class QuestManager(private val questStore: QuestStore) {

    private var questStates: Map<String, QuestState> = questStore.getStates()

    fun getAvailableQuests(
        talkable: Talkable,
        year: Int,
        season: Season,
        day: Int
    ): List<QuestDefinition> {
        talkable.nameId

        return questStore
            .getAvailable(
                talkable.nameId,
                year,
                season,
                day
            )
    }

    fun startQuest(id: String) {
        questStates[id]?.status = QuestStatus.IN_PROGRESS
    }

    fun updateProgress(id: String, amount: Int) {
        val state = questStates[id] ?: return
        val def = questStore.getById(id) ?: return
        state.progress += amount
        if (state.progress >= def.questRequirement.count) {
            completeQuest(id)
        }
    }

    private fun completeQuest(id: String) {
        questStates[id]?.status = QuestStatus.COMPLETED
    }
    /*


        fun startQuest(id: String) {
            states[id] = QuestState(id, QuestStatus.IN_PROGRESS)
        }

        fun updateProgress(id: String, amount: Int) {
            val state = states[id] ?: return
            val def = definitions[id] ?: return
            state.progress += amount
            if (state.progress >= def.requirements.count) {
                completeQuest(id)
            }
        }

        fun completeQuest(id: String) {
            states[id]?.status = QuestStatus.COMPLETED
            // give rewards
        }

        fun getState(id: String): QuestState? = states[id]*/
}
