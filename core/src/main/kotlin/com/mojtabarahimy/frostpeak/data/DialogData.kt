package com.mojtabarahimy.frostpeak.data

import com.mojtabarahimy.frostpeak.controller.dialog.DialogLine

data class DialogData(val npc: String, val dialogs: Map<String, List<DialogLine>>)
