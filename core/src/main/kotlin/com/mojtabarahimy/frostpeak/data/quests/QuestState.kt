package com.mojtabarahimy.frostpeak.data.quests

data class QuestState(
    val questId: String,
    var status: QuestStatus,
    var progress: Int = 0
)
