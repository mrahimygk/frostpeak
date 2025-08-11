package com.mojtabarahimy.frostpeak.entities.tools

interface ChangeableState {
    var stateChanged: Boolean
    fun changeState(newState: Boolean)
}
